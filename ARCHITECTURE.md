# MemeForge — Architecture Document

## 1. High-Level Overview

MemeForge sigue **MVVM + Repository** con las capas estándar de Android:
UI → ViewModel → Repository → Data Sources.

La característica central de diseño es la estrategia de carga
**stale-while-revalidate**: la app nunca bloquea esperando red; muestra
contenido cacheado inmediatamente y actualiza en background desde el CDN.

---

## 2. Architecture Diagram

```
╔══════════════════════════════════════════════════════════════════════════╗
║                         MEMEFORGE ANDROID                                ║
║                                                                          ║
║  ┌─────────────────────────────── UI LAYER ──────────────────────────┐  ║
║  │  Jetpack Compose + Material 3                                      │  ║
║  │                                                                    │  ║
║  │  MainActivity                                                      │  ║
║  │       └── AppNavGraph (Navigation Compose)                         │  ║
║  │               ├── HomeScreen ──────────────── HomeViewModel        │  ║
║  │               │   ├── ScrollableTabRow         ├─ collect          │  ║
║  │               │   ├── SearchBar                │  streamTemplates()│  ║
║  │               │   ├── LazyVerticalGrid          ├─ filter/search   │  ║
║  │               │   │   └── TemplateCard          └─ UiState Flow    │  ║
║  │               │   └── AdBanner (bottom)                            │  ║
║  │               │                                                    │  ║
║  │               └── EditorScreen ─────────────── EditorViewModel     │  ║
║  │                   ├── MemeCanvas (preview)       ├─ loadTemplate() │  ║
║  │                   ├── OutlinedTextField(s)        ├─ updateText()  │  ║
║  │                   ├── [Preview] [Guardar]         ├─ generatePreview│  ║
║  │                   ├── [Compartir]                 ├─ save()        │  ║
║  │                   └── [Crear Sticker]             └─ UiState Flow  │  ║
║  └──────────────────────────────────┬─────────────────────────────────┘  ║
║                                     │ inject (@HiltViewModel)             ║
║  ┌──────────────────────────── DOMAIN LAYER ─────────────────────────┐  ║
║  │                                                                    │  ║
║  │  MemeRenderer                    StickerExporter                  │  ║
║  │  ─────────────                   ────────────────                 │  ║
║  │  Android Canvas API              Escala bitmap a 512×512          │  ║
║  │  Dibuja texto con stroke         Exporta WEBP_LOSSLESS (API 30+)  │  ║
║  │  sobre el bitmap fuente          o WEBP legacy (API 26-29)        │  ║
║  │  Posiciones relativas 0.0–1.0    Fondo transparente ARGB_8888     │  ║
║  └──────────────────────────────────┬─────────────────────────────────┘  ║
║                                     │                                     ║
║  ┌──────────────────────────── DATA LAYER ───────────────────────────┐  ║
║  │                                                                    │  ║
║  │  TemplateRepository  (@Singleton — Hilt SingletonComponent)       │  ║
║  │  ──────────────────────────────────────────────────────────────   │  ║
║  │                                                                    │  ║
║  │   streamTemplates(): Flow<List<MemeTemplate>>                     │  ║
║  │   ┌──────────────────────────────────────────────────────────┐   │  ║
║  │   │  emit #1 ─── loadCached()  ─── filesDir/templates_cache  │   │  ║
║  │   │              loadBundled() ─── res/raw/templates.json     │   │  ║
║  │   │              (primer install, sin caché)                  │   │  ║
║  │   │                                                           │   │  ║
║  │   │  emit #2 ─── HTTP GET ──── jsDelivr CDN ─── GitHub       │   │  ║
║  │   │              (actualiza caché en disco si difiere)        │   │  ║
║  │   └──────────────────────────────────────────────────────────┘   │  ║
║  │                                                                    │  ║
║  │   getTemplateById(id): MemeTemplate?                              │  ║
║  │   └── busca en liveTemplates (in-memory, CDN-aware)              │  ║
║  │                                                                    │  ║
║  │  Models                                                           │  ║
║  │  ─────────────────────────────────────────────────────────────   │  ║
║  │  MemeTemplate  ←── kotlinx.serialization  ───  JSON              │  ║
║  │  TextZone                                                         │  ║
║  │  MemeText                                                         │  ║
║  └──────────────────────────────────────────────────────────────────┘  ║
║                                                                          ║
║  ┌─────────────────────── INFRASTRUCTURE ────────────────────────────┐  ║
║  │                                                                    │  ║
║  │  Hilt (DI)            Coil 2.7              OkHttp 4.12           │  ║
║  │  ──────────           ─────────────         ──────────────        │  ║
║  │  AppModule            ImageLoader            Custom interceptor    │  ║
║  │  SingletonComponent   SubcomposeAsync        Referer + User-Agent  │  ║
║  │  @HiltViewModel       Image (thumbnails)     para imgflip.com     │  ║
║  │                       AsyncImage (editor)    DebugLogger (dev)    │  ║
║  │                       allowHardware(false)                        │  ║
║  │                       DebugLogger                                 │  ║
║  │                                                                    │  ║
║  │  DataStore Prefs      AdMob 23.0             FileProvider         │  ║
║  │  ──────────────       ──────────             ────────────         │  ║
║  │  meme_prefs           Banner (Home)          Comparte JPG         │  ║
║  │  (reservado para      (test IDs en debug,    desde cacheDir       │  ║
║  │   configuración)       real IDs en release)  Comparte WEBP        │  ║
║  │                                              (stickers)           │  ║
║  └──────────────────────────────────────────────────────────────────┘  ║
╚══════════════════════════════════════════════════════════════════════════╝
```

---

## 3. Data Flow

### 3.1 Inicio de la app (Home)

```
App cold start
      │
      ▼
HomeViewModel.init
      │
      ├─ isRefreshing = true
      │
      └─ repository.streamTemplates().collect { }
              │
              ├──[emit #1 ~0ms]──▶ loadCached() desde filesDir
              │                    ó loadBundled() (primer install)
              │                         │
              │                         ▼
              │                   HomeUiState actualizado
              │                   isRefreshing = false
              │                   Grid renderiza thumbnails
              │
              └──[emit #2 ~500ms-8s]──▶ HTTP GET jsDelivr CDN
                                              │
                                    ┌─────────┴──────────┐
                                  éxito               falla
                                    │                   │
                             decode JSON          sin cambios
                             write cache          (lista actual ok)
                             liveTemplates update
                             emit nuevos templates
```

### 3.2 Edición de un meme

```
Usuario toca tarjeta
      │
      ▼
NavController.navigate("editor/{id}")
      │
      ▼
EditorViewModel.loadTemplate(id)
      │
      └─ repository.getTemplateById(id)  ← busca en liveTemplates
              │
              ▼
      Coil descarga imagen (OkHttp + caché de Coil)
      sourceBitmap disponible en EditorScreen
              │
              ├─ [Preview]  MemeRenderer.renderMeme()
              │             Canvas dibuja textos sobre bitmap
              │             Resultado → previewBitmap en UiState
              │
              ├─ [Guardar]  MemeRenderer.renderMeme()
              │             MediaStore.insert()  → galería (Pictures/MemeForge)
              │             IS_PENDING = 1 → write → IS_PENDING = 0 (API 29+)
              │
              ├─ [Compartir] savedUri ó File(cacheDir, "meme_share.jpg")
              │              Intent.ACTION_SEND + FileProvider
              │
              └─ [Sticker]  StickerExporter.exportAsWebP()
                            Canvas 512×512, bitmap escalado centrado
                            compress WEBP_LOSSLESS / WEBP legacy
                            FileProvider → Intent.ACTION_SEND
```

### 3.3 Agente de templates (scripts/add_template.py)

```
$ python3 scripts/add_template.py <url> <section>
      │
      ├─ Claude Vision API (claude-sonnet-4-5)
      │  Analiza imagen → name, textZones con coords relativas
      │
      ├─ Actualiza templates/templates.json        ← sirve jsDelivr
      │  Actualiza app/src/main/res/raw/templates.json  ← bundled seed
      │
      └─ git add → git commit → git push origin main
              │
              └─ jsDelivr CDN propaga (~5-10 min)
                      │
                      └─ próximo streamTemplates() lo incluye
                         sin recompilar la app
```

---

## 4. Storage Map

| Storage | Qué guarda | Lifecycle |
|---------|-----------|-----------|
| `res/raw/templates.json` | Semilla bundled en APK | Persiste mientras no se actualiza la app |
| `filesDir/templates_cache.json` | Último fetch exitoso del CDN | Persiste entre sesiones; se sobreescribe en cada fetch exitoso |
| `cacheDir/meme_share.jpg` | Meme temporal para compartir | Puede ser borrado por el sistema |
| `cacheDir/sticker.webp` | Sticker temporal para compartir | Puede ser borrado por el sistema |
| `MediaStore` (galería) | Memes guardados por el usuario | Persiste; directorio `Pictures/MemeForge` |
| DataStore Prefs | Reservado para configuración | Persiste entre sesiones |

---

## 5. Tech Stack

### Android / Kotlin

| Tecnología | Versión | Rol |
|-----------|---------|-----|
| **Kotlin** | 1.9.22 | Lenguaje principal |
| **Android Gradle Plugin** | 8.9.2 | Build system |
| **compileSdk / targetSdk** | 34 (Android 14) | API target |
| **minSdk** | 26 (Android 8.0) | Soporte mínimo |
| **Kotlin Coroutines** | (vía lifecycle) | Async: Flow, StateFlow, launch, withContext |
| **kotlinx.serialization** | 1.6.3 | JSON parse de templates.json |

### UI

| Tecnología | Versión | Rol |
|-----------|---------|-----|
| **Jetpack Compose BOM** | 2024.04.00 | UI declarativa |
| **Compose Material 3** | (BOM) | Design system: Scaffold, Card, TabRow, etc. |
| **Compose Foundation** | (BOM) | LazyVerticalGrid, ScrollableTabRow |
| **Navigation Compose** | 2.7.7 | Navegación type-safe entre pantallas |
| **Activity Compose** | 1.8.2 | `setContent {}` desde Activity |

### Architecture

| Tecnología | Versión | Rol |
|-----------|---------|-----|
| **Hilt** | 2.51 | Dependency Injection (Application, ViewModel, Singleton) |
| **Hilt Navigation Compose** | 1.2.0 | `hiltViewModel()` en Composables |
| **Lifecycle ViewModel Compose** | 2.7.0 | `collectAsState()`, ViewModel scoping |
| **DataStore Preferences** | 1.1.0 | Almacenamiento clave-valor (reservado) |

### Networking & Images

| Tecnología | Versión | Rol |
|-----------|---------|-----|
| **Coil** | 2.7.0 | Carga de imágenes: `SubcomposeAsyncImage`, `AsyncImage` |
| **OkHttp** | 4.12.0 | HTTP client subyacente de Coil; interceptor custom |
| **HttpURLConnection** | (Android) | Fetch directo del CDN en TemplateRepository |

### Media

| Tecnología | Versión | Rol |
|-----------|---------|-----|
| **Android Canvas API** | (Android) | MemeRenderer: dibuja texto sobre Bitmap |
| **MediaStore API** | (Android) | Guarda memes en galería (`Pictures/MemeForge`) |
| **FileProvider** | (AndroidX) | URI seguras para Intent de compartir |
| **Bitmap.compress** | (Android) | JPEG 95% (guardar/compartir), WEBP (stickers) |

### Monetización

| Tecnología | Versión | Rol |
|-----------|---------|-----|
| **Google AdMob** | 23.0.0 | Banner en HomeScreen (test IDs en debug) |

### Backend / Content Delivery

| Tecnología | Rol |
|-----------|-----|
| **GitHub** (`hernancasla/meme-forge`) | Source of truth para templates.json |
| **jsDelivr CDN** | Sirve templates.json con caché global (~5-10 min propagación) |
| **Imgflip CDN** (`i.imgflip.com`, Cloudflare) | Sirve las imágenes de los memes |

### Tooling

| Tecnología | Versión | Rol |
|-----------|---------|-----|
| **Python 3** | ≥3.10 | Script `add_template.py` |
| **Anthropic SDK (Python)** | ≥0.40 | Claude Vision API para el agente de templates |
| **claude-sonnet-4-5** | — | Analiza imágenes, infiere name + textZones |

---

## 6. Dependency Injection Graph

```
SingletonComponent (app lifetime)
      │
      ├── TemplateRepository  (@Singleton)
      │       └── Context (ApplicationContext)
      │
      └── DataStore<Preferences>  (@Singleton)
              └── Context (ApplicationContext)

ViewModelComponent (ViewModel lifetime)
      │
      ├── HomeViewModel  (@HiltViewModel)
      │       └── TemplateRepository
      │
      └── EditorViewModel  (@HiltViewModel)
              ├── TemplateRepository
              └── Context (ApplicationContext)
```

---

## 7. Key Design Decisions

### Stale-While-Revalidate
La estrategia de carga evita el dilema "esperar red vs. mostrar contenido
viejo". El Flow emite dos veces: caché del disco (instantáneo) y luego CDN
(actualizado). El usuario nunca ve pantalla en blanco.

### Bundled JSON como semilla, no como fuente de verdad
`res/raw/templates.json` solo se usa en la primera instalación cuando no
hay caché en disco. Después, el caché en disco toma el control. Esto significa
que agregar templates al repositorio de GitHub no requiere recompilar la app.

### `@Singleton` en TemplateRepository
Garantiza que `liveTemplates` sea una referencia única compartida entre
`HomeViewModel` y `EditorViewModel`. Sin esto, el editor no encontraría
templates que llegaron solo vía CDN (no están en el APK bundled).

### `allowHardware(false)` en Coil
Las imágenes cargadas para el editor necesitan copiarse a un `Canvas`
de software (MemeRenderer). Las hardware bitmaps no son dibujables en
Canvas de software → forzar software config en todos los requests.

### FileProvider para compartir
Android 7+ requiere `content://` URIs para compartir archivos entre apps.
`FileProvider` expone `cacheDir` de forma segura con `FLAG_GRANT_READ_URI_PERMISSION`.
