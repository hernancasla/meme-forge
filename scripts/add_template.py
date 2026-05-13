#!/usr/bin/env python3
"""
add_template.py — Agente que analiza una imagen de meme con Claude Vision,
genera la entrada del template y la pushea a main automáticamente.

Requiere:
    pip install anthropic

Variables de entorno:
    ANTHROPIC_API_KEY  — clave de la API de Anthropic (obligatorio)

Uso:
    python3 scripts/add_template.py <image_url> <section>
    python3 scripts/add_template.py <image_url> <section> --dry-run

Secciones válidas:
    simpsons, classic, trending, reaction

Ejemplos:
    python3 scripts/add_template.py "https://i.imgflip.com/xyz.jpg" "simpsons"
    python3 scripts/add_template.py "https://i.imgflip.com/abc.jpg" "classic" --dry-run
"""

import base64
import json
import os
import re
import subprocess
import sys
import urllib.request
from pathlib import Path

# ---------------------------------------------------------------------------
# Config
# ---------------------------------------------------------------------------

MODEL = "claude-sonnet-4-5"
VALID_CATEGORIES = {"simpsons", "classic", "trending", "reaction"}

SCRIPT_DIR = Path(__file__).parent.resolve()
PROJECT_ROOT = SCRIPT_DIR.parent
TEMPLATES_PUBLIC  = PROJECT_ROOT / "templates" / "templates.json"
TEMPLATES_BUNDLED = PROJECT_ROOT / "app" / "src" / "main" / "res" / "raw" / "templates.json"


# ---------------------------------------------------------------------------
# Vision analysis
# ---------------------------------------------------------------------------

PROMPT = """\
Analizá esta imagen de meme. La sección donde irá es: "{section}".

Devolvé ÚNICAMENTE un objeto JSON válido con esta estructura:
{{
  "name": "Nombre descriptivo del meme en español (2–5 palabras)",
  "textZones": [
    {{
      "id": "identificador_corto",
      "label": "Etiqueta en español para el editor",
      "defaultX": 0.5,
      "defaultY": 0.1,
      "defaultText": ""
    }}
  ]
}}

Instrucciones:
- "name": nombre reconocible del meme, en español, corto.
- "textZones": identificá las zonas donde este meme lleva texto habitualmente.
  - "id": corto, en inglés, minúsculas — top, bottom, left, right, panel1, line, sign, etc.
  - "label": etiqueta en español que verá el usuario al editar.
  - "defaultX" / "defaultY": posición relativa del centro del bloque (0.0 = izq/arriba, 1.0 = der/abajo).
  - "defaultText": texto de ejemplo si existe un texto típico; si no, cadena vacía "".
- Solo incluí las zonas que realmente se usan en este tipo de meme.
- No incluyas markdown, bloques de código ni explicaciones extra; solo el JSON.\
"""


def _call_claude(client, image_source: dict, section: str) -> dict:
    """Send image + prompt to Claude and parse the returned JSON."""
    import anthropic  # imported here so the import error is clear

    response = client.messages.create(
        model=MODEL,
        max_tokens=1024,
        messages=[
            {
                "role": "user",
                "content": [
                    {"type": "image", "source": image_source},
                    {"type": "text", "text": PROMPT.format(section=section)},
                ],
            }
        ],
    )

    raw = response.content[0].text.strip()
    # Strip markdown code fences if Claude added them
    raw = re.sub(r"^```(?:json)?\s*\n?", "", raw, flags=re.MULTILINE)
    raw = re.sub(r"\n?```\s*$", "", raw, flags=re.MULTILINE)
    return json.loads(raw.strip())


def _download_base64(url: str) -> tuple[str, str]:
    """Download image bytes and return (base64_string, media_type)."""
    headers = {
        "User-Agent": (
            "Mozilla/5.0 (Linux; Android 14) "
            "AppleWebKit/537.36 (KHTML, like Gecko) "
            "Chrome/120.0.0.0 Mobile Safari/537.36"
        )
    }
    req = urllib.request.Request(url, headers=headers)
    with urllib.request.urlopen(req, timeout=30) as resp:
        data = resp.read()
        media_type = resp.headers.get("Content-Type", "image/jpeg").split(";")[0].strip()
    return base64.b64encode(data).decode(), media_type


def analyze_image(image_url: str, section: str) -> dict:
    """
    Analyze a meme image with Claude Vision and return template metadata.

    Tries direct URL first; falls back to downloading + base64 if the API
    cannot fetch the URL itself.
    """
    try:
        import anthropic
    except ImportError:
        print("❌  El paquete 'anthropic' no está instalado.")
        print("   Instalalo con:  pip install anthropic")
        sys.exit(1)

    client = anthropic.Anthropic()

    # --- Attempt 1: URL source (no download needed) ---
    try:
        print("    → enviando URL directamente a la API…")
        return _call_claude(
            client,
            {"type": "url", "url": image_url},
            section,
        )
    except Exception as exc:
        print(f"    → URL directa falló ({type(exc).__name__}), descargando imagen…")

    # --- Attempt 2: base64 fallback ---
    b64_data, media_type = _download_base64(image_url)
    return _call_claude(
        client,
        {"type": "base64", "media_type": media_type, "data": b64_data},
        section,
    )


# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------

def slugify(text: str) -> str:
    """Convert a human-readable name to a safe ID slug."""
    text = text.lower().strip()
    for src, dst in [
        ("áàäâ", "a"), ("éèëê", "e"), ("íìïî", "i"),
        ("óòöô", "o"), ("úùüû", "u"), ("ñ", "n"),
    ]:
        for ch in src:
            text = text.replace(ch, dst)
    text = re.sub(r"[^\w\s]", "", text)
    text = re.sub(r"\s+", "_", text)
    return text.strip("_")


def unique_id(name: str, existing_ids: set[str]) -> str:
    base = slugify(name)
    if base not in existing_ids:
        return base
    counter = 2
    while f"{base}_{counter}" in existing_ids:
        counter += 1
    return f"{base}_{counter}"


def load_templates(path: Path) -> list:
    return json.loads(path.read_text(encoding="utf-8"))


def save_templates(path: Path, templates: list) -> None:
    path.write_text(
        json.dumps(templates, ensure_ascii=False, indent=2) + "\n",
        encoding="utf-8",
    )


def git(*args: str) -> None:
    result = subprocess.run(
        ["git", *args],
        cwd=PROJECT_ROOT,
        check=True,
        capture_output=True,
        text=True,
    )
    if result.stdout.strip():
        print("   ", result.stdout.strip())


# ---------------------------------------------------------------------------
# Main
# ---------------------------------------------------------------------------

def main() -> None:
    dry_run = "--dry-run" in sys.argv
    positional = [a for a in sys.argv[1:] if not a.startswith("--")]

    if len(positional) != 2:
        print(__doc__)
        sys.exit(1)

    image_url = positional[0]
    section   = positional[1].lower()

    if section not in VALID_CATEGORIES:
        print(f"❌  Sección '{section}' no es válida.")
        print(f"   Opciones: {', '.join(sorted(VALID_CATEGORIES))}")
        sys.exit(1)

    if not os.environ.get("ANTHROPIC_API_KEY"):
        print("❌  Falta la variable de entorno ANTHROPIC_API_KEY.")
        sys.exit(1)

    # ---- Analysis --------------------------------------------------------
    print(f"\n🔍  Analizando imagen con Claude Vision ({MODEL})…")
    print(f"    URL     : {image_url}")
    print(f"    Sección : {section}")

    try:
        analysis = analyze_image(image_url, section)
    except json.JSONDecodeError as exc:
        print(f"❌  Claude no devolvió JSON válido: {exc}")
        sys.exit(1)
    except Exception as exc:
        print(f"❌  Error inesperado al analizar la imagen: {exc}")
        sys.exit(1)

    name: str        = analysis["name"]
    text_zones: list = analysis["textZones"]

    print(f"\n✅  Identificado: \"{name}\"")
    for z in text_zones:
        print(f"    • {z['id']:10s}  ({z['defaultX']:.2f}, {z['defaultY']:.2f})  '{z['label']}'")

    # ---- Build entry -----------------------------------------------------
    templates    = load_templates(TEMPLATES_PUBLIC)
    existing_ids = {t["id"] for t in templates}
    entry_id     = unique_id(name, existing_ids)

    new_entry = {
        "id"        : entry_id,
        "name"      : name,
        "imageUrl"  : image_url,
        "category"  : section,
        "textZones" : text_zones,
    }

    print(f"\n📋  Entrada generada (id: {entry_id}):")
    print(json.dumps(new_entry, ensure_ascii=False, indent=2))

    if dry_run:
        print("\n⚠️   Modo --dry-run activo — no se escribió nada ni se commiteó.")
        return

    # ---- Update files ----------------------------------------------------
    templates.append(new_entry)
    save_templates(TEMPLATES_PUBLIC,  templates)
    save_templates(TEMPLATES_BUNDLED, templates)

    print(f"\n💾  Archivos actualizados:")
    print(f"    {TEMPLATES_PUBLIC.relative_to(PROJECT_ROOT)}")
    print(f"    {TEMPLATES_BUNDLED.relative_to(PROJECT_ROOT)}")

    # ---- Git commit + push -----------------------------------------------
    print("\n🚀  Commiteando y pusheando a main…")
    try:
        git("add",
            str(TEMPLATES_PUBLIC.relative_to(PROJECT_ROOT)),
            str(TEMPLATES_BUNDLED.relative_to(PROJECT_ROOT)))
        git("commit", "-m", f"feat: add meme template '{name}'")
        git("push", "origin", "main")
    except subprocess.CalledProcessError as exc:
        print(f"❌  Error de git: {exc.stderr.strip()}")
        sys.exit(1)

    cdn_url = (
        "https://cdn.jsdelivr.net/gh/hernancasla/meme-forge@main"
        "/templates/templates.json"
    )
    print(f"\n🎉  ¡Listo! Template '{name}' (id: {entry_id}) pusheado a main.")
    print(f"    Disponible vía CDN en ~10 min:")
    print(f"    {cdn_url}")


if __name__ == "__main__":
    main()
