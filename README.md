# The Quantum Void

*An AE2-themed void dimension addon for Applied Energistics 2.*

Explore, farm flawless certus quartz, and face down corrupted network fragments to reach the
Fractured Core — a void dimension of budding quartz islands and glowing fluix circuitry, built
around AE2's own identity instead of a generic "void islands" reskin.

**Status:** Phase 0 (prototype) — dimension registration and portal entry/exit work; procedural
world-gen, resources, structures, and mobs are not implemented yet. See the roadmap below.

## Requirements

| | |
|---|---|
| Minecraft | 26.1.2 |
| Mod loader | NeoForge 26.1.2.84+ |
| Hard dependency | Applied Energistics 2 26.1.10-beta+ |

## Getting in

A **Quantum Pearl** (Ender Pearl + a linked pair of Quantum Entangled Singularities, crafted on a
powered ME network with an active Quantum Ring) placed into every block of a completed **Quantum
Portal Frame** (2x2 opening, 8 frame blocks) opens a portal to the Quantum Void. No ongoing power
draw once active — build a return frame on the other side to come back.

## Roadmap

| Phase | Deliverable |
|---|---|
| 0 — Prototype | Dimension registration, portal entry/exit, static void *(current)* |
| 1 — MVP | Procedural island generation, budding certus, Void Sky Stone |
| 2 — Content pass | Fluix veins, minor ruin structures, Fragment mobs + channel-drain |
| 3 — Capstone | The Fractured Core, boss, Singularity Seed |
| 4 — Polish | Ambient effects, sound design, balance pass |

Full design decisions and rationale: [docs/DESIGN.md](docs/DESIGN.md).
Contribution conventions and versioning scheme: [CONTRIBUTING.md](CONTRIBUTING.md).

## Building

```
./gradlew build          # Linux/macOS/Git Bash
gradlew.bat build        # Windows cmd/PowerShell
```

First run downloads the Gradle distribution and NeoForge userdev artifacts and decompiles
Minecraft — expect it to take a while. `./gradlew --refresh-dependencies` refreshes a stale
dependency cache; `./gradlew clean` resets build output without touching source.

## Mapping names

This project uses Mojang's official mapping names for methods and fields in the Minecraft
codebase. These names are covered by a specific license — see
[NeoForm/Mojang.md](https://github.com/NeoForged/NeoForm/blob/main/Mojang.md).

## Resources

- [NeoForged docs](https://docs.neoforged.net/)
- [NeoForged Discord](https://discord.neoforged.net/)
- [AE2 addon/API docs](https://github.com/AppliedEnergistics/Applied-Energistics-2/blob/main/API.md)
