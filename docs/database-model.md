# Database Model

This document is the authoritative reference for Comixa's data schema.
**Keep it in sync whenever a Room entity, field, or migration changes.**
The future `comixa-desktop` project must match these definitions exactly.

---

## Tables

### `comic_books`

| Column      | Type    | Constraints              | Notes                            |
|-------------|---------|--------------------------|----------------------------------|
| id          | INTEGER | PRIMARY KEY AUTOINCREMENT |                                  |
| title       | TEXT    | NOT NULL                 |                                  |
| filePath    | TEXT    | NOT NULL, UNIQUE         | Absolute path on device          |
| format      | TEXT    | NOT NULL                 | Enum string: CBZ, ZIP, PDF       |
| pageCount   | INTEGER | NOT NULL, DEFAULT 0      |                                  |
| coverPath   | TEXT    | NULLABLE                 | Cached cover thumbnail path      |
| addedAt     | INTEGER | NOT NULL, DEFAULT 0      | Unix epoch milliseconds          |

---

### `reading_progress`

| Column      | Type    | Constraints                                    | Notes                   |
|-------------|---------|------------------------------------------------|-------------------------|
| bookId      | INTEGER | PRIMARY KEY, FK → comic_books(id) CASCADE DEL  |                         |
| currentPage | INTEGER | NOT NULL                                       | 0-based page index      |
| totalPages  | INTEGER | NOT NULL                                       |                         |
| lastReadAt  | INTEGER | NOT NULL, DEFAULT 0                            | Unix epoch milliseconds |

---

### `bookmarks`

| Column     | Type    | Constraints                                    | Notes                   |
|------------|---------|------------------------------------------------|-------------------------|
| id         | INTEGER | PRIMARY KEY AUTOINCREMENT                      |                         |
| bookId     | INTEGER | NOT NULL, FK → comic_books(id) CASCADE DEL     | Indexed                 |
| pageIndex  | INTEGER | NOT NULL                                       | 0-based                 |
| label      | TEXT    | NOT NULL, DEFAULT ""                           | User-defined label      |
| createdAt  | INTEGER | NOT NULL, DEFAULT 0                            | Unix epoch milliseconds |

---

## Enums

### `ComicFormat`
Stored as TEXT string in the database.

| Value | Description          |
|-------|----------------------|
| CBZ   | ZIP archive of images |
| ZIP   | Same as CBZ           |
| PDF   | PDF document          |

### `ReadingDirection`
Stored in user preferences (DataStore), not in Room.

| Value          | Description          |
|----------------|----------------------|
| LEFT_TO_RIGHT  | Western reading order |
| RIGHT_TO_LEFT  | Manga reading order   |
| TOP_TO_BOTTOM  | Webtoon/scroll mode   |

---

## Room Database

- **Class:** `ComixaDatabase`
- **Version:** 2
- **Schema export:** enabled (`schemas/` directory)
- **Entities:** `ComicBookEntity`, `ReadingProgressEntity`, `BookmarkEntity`

---

## Migration history

| From → To | Change                     |
|-----------|----------------------------|
| 1 → 2     | *(document here as needed)* |

---

## Update checklist

When changing any entity:
1. Bump the Room database version.
2. Write a `Migration` object.
3. Update the table definition above.
4. Update `docs/sync-model.md` if the field is part of the sync contract.
