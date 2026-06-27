# Sync Conflicts

This document defines how Comixa resolves conflicts when the same data is modified
on multiple devices before a sync occurs.

**Update this file when a new conflict scenario is identified or a resolution strategy changes.**

---

## General principle

> **Last-write-wins by `last_read_at` / `created_at` timestamp.**
> Clocks on mobile devices can drift. Prefer server time when a server is involved;
> use device-local UTC otherwise and accept occasional minor drift.

---

## Per-entity conflict rules

### ReadingProgress

A user may read on Android, then switch to Desktop without syncing, creating two divergent progress records.

| Scenario                                          | Resolution                                            |
|---------------------------------------------------|-------------------------------------------------------|
| Android ahead, Desktop behind                     | Take Android record (higher `last_read_at`)           |
| Desktop ahead, Android behind                     | Take Desktop record (higher `last_read_at`)           |
| Same `last_read_at` (clock collision), pages differ | Take higher `current_page`                          |
| One device reports `isCompleted`, the other doesn't | Completed wins — never regress a finished book     |

```
resolved = max(android, desktop) by last_read_at
           then max(current_page) on tie
           then completed=true wins unconditionally
```

---

### Bookmarks

Bookmarks are additive — a user creates them on one device and expects to see them everywhere.
Deletion is the only conflict-prone operation.

| Scenario                                             | Resolution                                          |
|------------------------------------------------------|-----------------------------------------------------|
| Same bookmark (by UUID) exists on both devices       | Identical — no conflict                             |
| Bookmark deleted on Device A, still present on B     | Deletion wins (tombstone approach — see below)      |
| Bookmark label edited on both devices                | Last-write-wins by `updated_at` (add field if needed) |

**Tombstoning:** When a bookmark is deleted, record `(id, deleted_at)` in a `deleted_bookmarks` log
instead of hard-deleting it immediately. During sync, propagate deletions before additions,
then prune tombstones older than 30 days.

> Tombstone table does not yet exist. Add it before implementing sync.
> Add `docs/database-model.md` entry when the table is created.

---

### ComicBook metadata

Title and format are rarely edited. If they diverge:

| Field      | Resolution                       |
|------------|----------------------------------|
| title      | Last-write-wins by `updated_at`  |
| format     | Device that scanned the file wins (it has ground truth) |
| page_count | Higher value wins (more complete scan) |

---

## Clock skew

Devices may have clocks off by minutes or even hours.

- All timestamps stored and transmitted as **UTC epoch milliseconds**.
- When a server mediates sync, the server stamps the arrival time and uses it as the authoritative `last_synced_at`.
- For direct P2P sync (Wi-Fi), trust the device that initiated the sync as the "leader" clock for that session.

---

## Schema versioning

When the sync DTO format changes in a breaking way (field renamed or removed):

1. Bump a `sync_schema_version` integer in the sync payload envelope.
2. Add a migration note in this file under the table below.
3. Old clients that don't understand the new version must refuse the payload gracefully (skip, don't crash).

| Version | Change                  | Date       |
|---------|-------------------------|------------|
| 1       | Initial schema          | (pending)  |

---

## Update checklist

- New entity added to sync → add a conflict rule section here.
- Breaking DTO change → add a schema version entry to the table above.
- New edge case discovered during testing → document the scenario and resolution.
