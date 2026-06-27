# Sync Model

This document defines the **shared data contract** between `comixa-android` and the future `comixa-desktop`.
Both platforms must serialize and deserialize these structures identically.

**Update this file whenever a synced field is added, renamed, removed, or its semantics change.**

---

## What gets synced

| Entity           | Synced | Notes                                           |
|------------------|--------|-------------------------------------------------|
| ReadingProgress  | YES    | Core use-case: continue reading across devices  |
| Bookmarks        | YES    | User-created markers should follow the user     |
| ComicBook        | PARTIAL| Metadata only (title, format, pageCount, addedAt); filePath is device-local and excluded |
| LibraryFolder    | NO     | Paths are device-local; not portable             |
| UserPreferences  | NO (MVP)| Reading direction, theme — skip for now        |

---

## Sync DTOs

These are the canonical JSON structures. They are **not** the same as Room entities.
Field names use `snake_case` in JSON.

### `ReadingProgressDto`

```json
{
  "book_id": "string (stable cross-device ID, see identity section)",
  "current_page": 42,
  "total_pages": 180,
  "last_read_at": 1719480000000
}
```

| Field         | Type   | Notes                          |
|---------------|--------|--------------------------------|
| book_id       | string | Stable cross-device identifier |
| current_page  | int    | 0-based page index             |
| total_pages   | int    |                                |
| last_read_at  | long   | Unix epoch milliseconds (UTC)  |

---

### `BookmarkDto`

```json
{
  "id": "uuid-string",
  "book_id": "string (stable cross-device ID)",
  "page_index": 17,
  "label": "Plot twist",
  "created_at": 1719480000000
}
```

| Field      | Type   | Notes                          |
|------------|--------|--------------------------------|
| id         | string | UUID v4, assigned on creation  |
| book_id    | string | Stable cross-device identifier |
| page_index | int    | 0-based                        |
| label      | string |                                |
| created_at | long   | Unix epoch milliseconds (UTC)  |

---

### `ComicBookMetaDto`

```json
{
  "book_id": "string",
  "title": "My Comic",
  "format": "CBZ",
  "page_count": 180,
  "added_at": 1719480000000
}
```

| Field      | Type   | Notes                                |
|------------|--------|--------------------------------------|
| book_id    | string | Stable cross-device identifier       |
| title      | string |                                      |
| format     | string | Enum: CBZ, ZIP, PDF                  |
| page_count | int    |                                      |
| added_at   | long   | Unix epoch milliseconds (UTC)        |

---

## Cross-device identity

The biggest challenge in sync is identifying the same comic on two different devices
where the local `filePath` differs (e.g., `/sdcard/comics/my.cbz` vs `C:\Comics\my.cbz`).

**Decision (to be finalized before implementing sync):**
Use a content hash (SHA-256 of the first N bytes of the file, or the full file) as `book_id`.
This is stable across renames and path differences as long as the file content is the same.

- Android: compute on scan, store alongside the Room `id`.
- Desktop: compute on import.
- If two devices have the same hash → same book, merge their progress/bookmarks.

> This field does not yet exist in the Android Room schema.
> Add it as `contentHash: String?` to `ComicBookEntity` before implementing sync.
> See `docs/database-model.md` for the schema update checklist.

---

## Sync transport (future, not MVP)

The exact transport is TBD. Candidates:
- Google Drive App Data folder (no user-visible files, free quota)
- Custom REST API
- Direct Wi-Fi / LAN (see `smb/` future module)

The DTO structures above must stay transport-agnostic.

---

## Update checklist

When the Android data model changes:
1. Check if the changed field is in a synced entity (table above).
2. If yes — update the DTO definition in this file.
3. If the change is breaking (rename/remove) — add an entry to `docs/sync-conflicts.md`.
4. When `comixa-desktop` exists, open a cross-repo issue to keep both sides in sync.
