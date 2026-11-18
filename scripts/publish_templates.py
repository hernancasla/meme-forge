#!/usr/bin/env python3
"""
publish_templates.py — Publica templates/templates.json después de editarlo a mano
(agregar, borrar o modificar plantillas).

Pasos:
    1. Valida el JSON (estructura, campos, ids únicos, coordenadas 0–1).
    2. Copia templates/templates.json → app/src/main/res/raw/templates.json.
    3. Commitea solo esos dos archivos y pushea a main.
    4. Purga el caché de jsDelivr.
    5. Verifica que el CDN ya sirva la versión nueva.

Uso:
    python3 scripts/publish_templates.py
    python3 scripts/publish_templates.py -m "fix: quitar plantillas con armas"
    python3 scripts/publish_templates.py --dry-run      # solo valida y muestra cambios vs CDN
    python3 scripts/publish_templates.py --purge-only   # solo purga + verifica (sin git)
"""

import json
import shutil
import subprocess
import sys
import time
from pathlib import Path

# ---------------------------------------------------------------------------
# Config
# ---------------------------------------------------------------------------

VALID_CATEGORIES = {"simpsons", "classic", "trending", "reaction"}
DEFAULT_COMMIT_MESSAGE = "chore: update meme templates"
BRANCH = "main"

CDN_URL = (
    "https://cdn.jsdelivr.net/gh/hernancasla/meme-forge@main"
    "/templates/templates.json"
)
PURGE_URL = CDN_URL.replace("cdn.jsdelivr.net", "purge.jsdelivr.net")
HTTP_TIMEOUT_SECONDS = 20
VERIFY_ATTEMPTS = 5
VERIFY_DELAY_SECONDS = 6

SCRIPT_DIR = Path(__file__).parent.resolve()
PROJECT_ROOT = SCRIPT_DIR.parent
TEMPLATES_PUBLIC  = PROJECT_ROOT / "templates" / "templates.json"
TEMPLATES_BUNDLED = PROJECT_ROOT / "app" / "src" / "main" / "res" / "raw" / "templates.json"

TEMPLATE_FIELDS = {"id": str, "name": str, "imageUrl": str, "category": str, "textZones": list}
ZONE_FIELDS = {"id": str, "label": str, "defaultX": (int, float), "defaultY": (int, float),
               "defaultText": str}


# ---------------------------------------------------------------------------
# Validation
# ---------------------------------------------------------------------------

def _check_fields(obj: dict, fields: dict, where: str) -> list[str]:
    errors = []
    for field, expected in fields.items():
        if field not in obj:
            errors.append(f"{where}: falta '{field}'")
        elif not isinstance(obj[field], expected) or isinstance(obj[field], bool):
            errors.append(f"{where}: '{field}' tiene tipo inválido")
    return errors


def _validate_zone(zone, where: str) -> list[str]:
    if not isinstance(zone, dict):
        return [f"{where}: la zona no es un objeto"]
    errors = _check_fields(zone, ZONE_FIELDS, where)
    for coord in ("defaultX", "defaultY", "maxWidthRatio", "maxHeightRatio"):
        value = zone.get(coord)
        if isinstance(value, (int, float)) and not 0 <= value <= 1:
            errors.append(f"{where}: '{coord}' = {value} fuera de rango 0–1")
    return errors


def _validate_template(template, index: int) -> list[str]:
    if not isinstance(template, dict):
        return [f"#{index}: la plantilla no es un objeto"]
    where = f"#{index} ({template.get('id', '?')})"
    errors = _check_fields(template, TEMPLATE_FIELDS, where)
    if template.get("category") not in VALID_CATEGORIES:
        errors.append(f"{where}: categoría '{template.get('category')}' no válida")
    if not str(template.get("imageUrl", "")).startswith("https://"):
        errors.append(f"{where}: imageUrl debe ser https://")
    zones = template.get("textZones")
    if isinstance(zones, list):
        if not zones:
            errors.append(f"{where}: textZones está vacío")
        for zone in zones:
            errors += _validate_zone(zone, f"{where}.{zone.get('id', '?') if isinstance(zone, dict) else '?'}")
    return errors


def validate(templates) -> list[str]:
    if not isinstance(templates, list):
        return ["El JSON raíz debe ser una lista"]
    errors = []
    seen = set()
    for index, template in enumerate(templates):
        errors += _validate_template(template, index)
        template_id = template.get("id") if isinstance(template, dict) else None
        if template_id in seen:
            errors.append(f"#{index}: id duplicado '{template_id}'")
        seen.add(template_id)
    return errors


# ---------------------------------------------------------------------------
# Git / HTTP helpers
# ---------------------------------------------------------------------------

def git(*args: str) -> str:
    result = subprocess.run(
        ["git", *args],
        cwd=PROJECT_ROOT,
        check=True,
        capture_output=True,
        text=True,
    )
    return result.stdout.strip()


def http_get_json(url: str):
    # curl usa los certificados del sistema; el Python de python.org en macOS
    # no los trae instalados y urllib falla con CERTIFICATE_VERIFY_FAILED.
    result = subprocess.run(
        ["curl", "-fsSL", "--max-time", str(HTTP_TIMEOUT_SECONDS), url],
        capture_output=True,
        text=True,
    )
    if result.returncode != 0:
        raise RuntimeError(result.stderr.strip() or f"curl salió con código {result.returncode}")
    return json.loads(result.stdout)


def fetch_json(url: str):
    # Query param único para no recibir una respuesta cacheada por proxies intermedios
    return http_get_json(f"{url}?t={int(time.time())}")


def ids_of(templates: list) -> list[str]:
    return [t["id"] for t in templates]


def print_diff(local_ids: list[str], remote_ids: list[str]) -> None:
    added = [i for i in local_ids if i not in set(remote_ids)]
    removed = [i for i in remote_ids if i not in set(local_ids)]
    print(f"    Local: {len(local_ids)} plantillas · CDN: {len(remote_ids)} plantillas")
    for template_id in added:
        print(f"    + {template_id}")
    for template_id in removed:
        print(f"    - {template_id}")
    if not added and not removed:
        print("    (mismos ids — puede haber cambios en nombres, zonas o URLs)")


# ---------------------------------------------------------------------------
# Steps
# ---------------------------------------------------------------------------

def step_validate() -> list:
    print("\n🔎  Validando templates/templates.json…")
    try:
        templates = json.loads(TEMPLATES_PUBLIC.read_text(encoding="utf-8"))
    except json.JSONDecodeError as exc:
        print(f"❌  JSON inválido: {exc}")
        sys.exit(1)
    errors = validate(templates)
    if errors:
        print(f"❌  {len(errors)} error(es):")
        for error in errors:
            print(f"    • {error}")
        sys.exit(1)
    print(f"    ✅  {len(templates)} plantillas válidas.")
    return templates


def step_show_diff(templates: list) -> None:
    print("\n📊  Cambios respecto de lo que sirve el CDN:")
    try:
        print_diff(ids_of(templates), ids_of(fetch_json(CDN_URL)))
    except Exception as exc:
        print(f"    ⚠️  No se pudo leer el CDN: {exc}")


def step_sync_bundled() -> None:
    shutil.copyfile(TEMPLATES_PUBLIC, TEMPLATES_BUNDLED)
    print(f"\n💾  Copiado a {TEMPLATES_BUNDLED.relative_to(PROJECT_ROOT)}")


def step_commit_and_push(message: str) -> None:
    paths = [str(p.relative_to(PROJECT_ROOT)) for p in (TEMPLATES_PUBLIC, TEMPLATES_BUNDLED)]
    try:
        branch = git("rev-parse", "--abbrev-ref", "HEAD")
        if branch != BRANCH:
            print(f"❌  Estás en la rama '{branch}'. El CDN lee de '{BRANCH}'.")
            sys.exit(1)
        if not git("status", "--porcelain", "--", *paths):
            print("\n🟰  Sin cambios en los JSON — no hay nada para commitear.")
        else:
            print("\n🚀  Commiteando y pusheando a main…")
            git("add", "--", *paths)
            # Commit solo de los JSON, aunque haya otros archivos en staging
            git("commit", "-m", message, "--", *paths)
            print(f"    {git('log', '-1', '--oneline')}")
        git("push", "origin", BRANCH)
        print("    ✅  Push OK.")
    except subprocess.CalledProcessError as exc:
        print(f"❌  Error de git: {(exc.stderr or exc.stdout).strip()}")
        sys.exit(1)


def step_purge() -> None:
    print("\n🌐  Purgando caché de jsDelivr…")
    try:
        body = http_get_json(PURGE_URL)
    except Exception as exc:
        print(f"    ⚠️  Purge falló: {exc}")
        print(f"    Reintentá a mano: {PURGE_URL}")
        return
    if body.get("status") == "finished":
        print("    ✅  Caché purgado.")
    else:
        print(f"    ⚠️  Respuesta inesperada: {json.dumps(body)[:200]}")


def step_verify(templates: list) -> bool:
    print("\n🔁  Verificando que el CDN sirva la versión nueva…")
    expected = ids_of(templates)
    for attempt in range(1, VERIFY_ATTEMPTS + 1):
        try:
            remote = ids_of(fetch_json(CDN_URL))
            if remote == expected:
                print(f"    ✅  CDN actualizado ({len(remote)} plantillas).")
                return True
            print(f"    … intento {attempt}/{VERIFY_ATTEMPTS}: el CDN todavía difiere")
        except Exception as exc:
            print(f"    … intento {attempt}/{VERIFY_ATTEMPTS}: {exc}")
        time.sleep(VERIFY_DELAY_SECONDS)
    print("    ⚠️  El CDN todavía no refleja los cambios. Volvé a correr con --purge-only en unos minutos.")
    return False


# ---------------------------------------------------------------------------
# Main
# ---------------------------------------------------------------------------

def parse_message(argv: list[str]) -> str:
    for flag in ("-m", "--message"):
        if flag in argv:
            index = argv.index(flag)
            if index + 1 >= len(argv) or not argv[index + 1].strip():
                print(f"❌  Falta el mensaje después de {flag}.")
                sys.exit(1)
            return argv[index + 1]
    return DEFAULT_COMMIT_MESSAGE


def main() -> None:
    argv = sys.argv[1:]
    if "-h" in argv or "--help" in argv:
        print(__doc__)
        return

    templates = step_validate()

    if "--dry-run" in argv:
        step_show_diff(templates)
        print("\n⚠️   Modo --dry-run — no se escribió, commiteó ni purgó nada.")
        return

    if "--purge-only" not in argv:
        step_show_diff(templates)
        step_sync_bundled()
        step_commit_and_push(parse_message(argv))

    step_purge()
    ok = step_verify(templates)
    print("\n🎉  ¡Listo! Los usuarios reciben la lista nueva al abrir la app." if ok else "")
    sys.exit(0 if ok else 2)


if __name__ == "__main__":
    main()
