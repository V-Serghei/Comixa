# Comixa

An original Android comic reader built with Kotlin and Jetpack Compose.

## What it does

Comixa lets you manage and read your local comic collection. It scans folders on your device, displays a clean library of covers, and provides a smooth reading experience with zoom, page navigation, progress tracking, and bookmarks.

## MVP Roadmap

| Phase | Scope |
|-------|-------|
| **MVP 1** (current) | Local CBZ/ZIP + PDF reading, library, reader, bookmarks, progress, Room DB |
| MVP 2 | CBR/RAR, cover extraction, page cache |
| MVP 3 | Google Drive / OneDrive / Dropbox |
| MVP 4 | PC Wi-Fi sharing via SMB / WebDAV |
| MVP 5 | AI translation, OCR |

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **DI**: Hilt
- **DB**: Room
- **Async**: Coroutines + Flow
- **Images**: Coil
- **PDF**: Android PdfRenderer
- **Archives**: zip4j (CBZ/ZIP)
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 36

## Architecture

Clean modular architecture. Each feature lives in its own module with a clear interface so future modules (cloud sources, OCR, AI translation) can be added without touching the core reader.

```
app/
├── core/          — shared models, utilities, base classes
├── data/          — Room database, repositories, file scanner
├── library/       — library screen and viewmodel
├── reader/        — reader screen, page renderer, zoom
└── settings/      — app settings
```

## Building

Requires Android Studio Hedgehog or later, JDK 17.

```bash
./gradlew assembleDebug
```

## License

MIT
