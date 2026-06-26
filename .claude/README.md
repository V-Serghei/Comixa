# .claude/ — Project Claude Config

This folder is tracked in git and shared across the team.

## What goes here

- `settings.json` — project-level Claude Code settings (permissions, hooks). Configure via `/config` or the update-config skill.
- `skills/` — custom slash commands for this project (future).
- `workflows/` — multi-agent workflow scripts (future).

## Useful commands

| Command | Purpose |
|---------|---------|
| `/code-review` | Review current diff for bugs and cleanups |
| `/security-review` | Security review of pending changes |
| `/run` | Launch the app / run build |
| `update-config` skill | Add permissions or hooks to settings.json |

## Project context

See `CLAUDE.md` in the repo root for architecture decisions, tech stack, and MVP scope.
