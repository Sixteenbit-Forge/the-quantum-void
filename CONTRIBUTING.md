# Contributing

Solo/small-team project conventions — kept light, adjust as the team grows.

## Branching

- `main` — always buildable, reflects the latest completed work.
- `phase/N-name` for a phase's worth of work (e.g. `phase/1-worldgen`), merged back to `main` once that phase milestone builds and runs cleanly.
- `feature/x` or `fix/x` for anything smaller, branched off `main`.

## Commits

[Conventional Commits](https://www.conventionalcommits.org/): `feat:`, `fix:`, `docs:`, `refactor:`, `chore:`, `build:`. Scope with the system touched when it helps, e.g. `feat(portal): add ring detection`.

## Versioning

SemVer via `mod_version` in `gradle.properties`, pre-1.0 during development. Version ranges track the phase roadmap from `docs/DESIGN.md`:

| Version range | Phase | Deliverable |
|---|---|---|
| 0.1.x | 0 — Prototype | Dimension registration + portal entry/exit, static void |
| 0.2.x | 1 — MVP | Procedural island generation, basic resource blocks |
| 0.3.x | 2 — Content pass | Fluix veins, minor ruin structures, Fragment mobs |
| 0.4.x | 3 — Capstone | Fractured Core, boss, Singularity Seed |
| 0.5.x | 4 — Polish | Ambient effects, sound, balance pass |
| 1.0.0 | — | First public release |

Tag releases `vX.Y.Z` once a phase milestone is stable.

## Design decisions

Locked decisions live in [docs/DESIGN.md](docs/DESIGN.md) — check there before changing an established mechanic, and append new decisions there rather than letting them live only in commit messages or chat history.
