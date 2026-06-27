# Comixa — Codex Context

## What this project is

An original Android comic reader app. **Not a clone.** The goal is a polished, modular reader for local comic files that can later be extended with cloud sources and AI features.

## Current phase: MVP 1 — Local Reader Core

Implement and stabilize:
- Local folder selection and file scanning
- CBZ/ZIP reading (zip4j)
- PDF reading (Android PdfRenderer)
- Library screen with covers
- Reader screen with page navigation
- Zoom and fit modes
- Reading progress (per-book, per-page)
- Bookmarks
- Room database
- Hilt dependency injection

Do NOT implement in MVP 1:
- Cloud sources (Google Drive, OneDrive, Dropbox)
- SMB / WebDAV
- CBR/RAR
- OCR or AI translation
- Network sync

## Tech stack

| Layer | Tech |
|-------|------|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| DI | Hilt |
| DB | Room + Kotlin Coroutines/Flow |
| Images | Coil |
| PDF | Android PdfRenderer |
| Archives | zip4j |
| Background work | WorkManager |
| Min SDK | 26 |

## Module layout (target)

```
app/
├── core/          — ComicBook, Page, ReadingProgress, Bookmark models + shared utils
├── data/          — Room DB (ComicDao, BookmarkDao), FileScanner, repositories
├── library/       — LibraryScreen (Compose), LibraryViewModel
├── reader/        — ReaderScreen (Compose), ReaderViewModel, PageRenderer, ZoomState
└── settings/      — SettingsScreen, SettingsViewModel, UserPreferences (DataStore)
```

## Code style rules

- Kotlin only — no Java
- Jetpack Compose for all UI — no XML layouts, no Fragments
- Unidirectional data flow: ViewModel → State → Composable
- Repository pattern — no direct DB access from ViewModels
- Coroutines + Flow — no callbacks, no LiveData
- No feature flags, no backwards-compat shims
- No comments unless the WHY is truly non-obvious

## Git workflow

- Main branch: `main`
- Feature branches: `feature/<short-name>`
- PR to `main` after review
- Do not push to main directly

## Future modules (do not implement yet, just keep the interfaces clean)

- `cloud/` — abstract ComicSource interface that cloud providers will implement
- `ocr/` — page text extraction
- `translation/` — AI translation layer
- `smb/` — Wi-Fi PC sharing
