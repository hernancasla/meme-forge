# Los cambios en `templates.json` no aparecen en la app

Checklist para forzar la actualización, en orden.

## 1. Verificar que el cambio esté pusheado a GitHub

```bash
git status
git log -1 -- templates/templates.json
```

El CDN (jsDelivr) lee de GitHub `@main`, no de tu disco local. Si no está
pusheado, nunca va a aparecer.

## 2. Purgar el cache de jsDelivr

jsDelivr cachea el archivo y no siempre lo refresca solo:

```bash
curl "https://purge.jsdelivr.net/gh/hernancasla/meme-forge@main/templates/templates.json"
```

Confirmar que sirvió:

```bash
curl -s "https://cdn.jsdelivr.net/gh/hernancasla/meme-forge@main/templates/templates.json" \
  | python3 -c "import json,sys; print(len(json.load(sys.stdin)))"
```

## 3. Limpiar el cache local de la app (el paso que casi siempre falta)

La app (`TemplateRepository.kt`) guarda una copia en disco
(`templates_cache.json`) y la muestra ANTES de intentar refrescar del CDN.
Si esa copia quedó vieja, se sigue viendo lo mismo aunque el CDN esté al día.

```bash
adb shell run-as com.memeforge rm -f /data/data/com.memeforge/files/templates_cache.json
adb shell am force-stop com.memeforge
adb shell monkey -p com.memeforge -c android.intent.category.LAUNCHER 1
```

O más simple: **Configuración → Apps → MemeForge → Borrar datos/cache**,
o desinstalar/reinstalar la app.

## 4. (Opcional) Sincronizar `app/src/main/res/raw/templates.json`

Es el fallback usado en la primera instalación o sin red. Si editás
`templates/templates.json` a mano, copiá el mismo contenido ahí también
(o usá `scripts/add_template.py`, que sincroniza los dos archivos solo).

## 5. Si las imágenes no cargan pero la lista sí aparece

Es un problema de red/DNS del emulador, no del JSON. Solución: cold boot
del emulador (Device Manager → ⋮ → "Cold Boot Now") o probar en un
dispositivo/emulador con internet real.
