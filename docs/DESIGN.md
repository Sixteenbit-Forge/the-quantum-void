# The Quantum Void — Design Decisions

Living log of locked design decisions. Each entry is final unless revisited explicitly.
Target: Minecraft 26.1.2, NeoForge 26.1.2-80+, AE2 26.1.10-beta+.

## Portal — Access & Return Trip

**Quantum Pearl** (new item)
- Recipe: 2x Ender Pearl + 1x linked pair of Quantum Entangled Singularities → 2x Quantum Pearl (linked pair).
- Craftable only via a network-connected crafting device on an ME network with an active, powered Quantum Ring formation. No power requirement after crafting.
- The two output pearls are linked to each other (mirrors Entangled Singularity pairing).

**Quantum Portal Frame** (new block)
- Modeled on End Portal Frame's activation pattern: holds one Quantum Pearl per frame block, portal activates when the full frame is filled.
- Frame size: 2x2 interior opening, 8 frame blocks total (2 per side, no corners) — half the material cost of vanilla's 3x3/12-frame End Portal, reflecting AE2's "efficient engineering" identity vs. vanilla's ritual scale.

**Activation & linking**
- Place a Quantum Pearl in every frame block of a completed 2x2 ring → portal activates, links to wherever its paired pearl resides (Quantum Void side or overworld side).
- Building the return portal: carry the paired pearl through, build a second frame on the other side, place the paired pearl to complete the link.
- No ongoing power draw to keep the portal open — avoids Nether-portal-style one-way jank.
- Breaking the link (pearl destroyed/removed and not replaced) deactivates the portal rather than stranding players; frame can be re-filled with a new linked pair to reactivate.

## World Generation

**Chunk generator**
- Custom `ChunkGenerator` subclass, direct hardcoded noise sampling for island footprint/tiering (à la vanilla `TheEndChunkGenerator`) — not the datapack density-function pipeline.

**Budding certus quartz**
- Two tiers: common non-flawless budding clusters scatter naturally on islands (ambient resource, no exploration gate); guaranteed flawless cluster reserved for structure loot (minor ruins + guaranteed at the Fractured Core).

**Wild fluix crystal growths**
- Two tiers: the glowing fluix veins running through the void (Section 3 visual identity) are themselves harvestable — the "circuit board" look doubles as ambient resource, not pure decoration. Denser/higher-yield growths cluster on and around structures as a farming incentive.

**Island tiers**
- Small islands: common, minor resources only.
- "Motherboard" islands (host structures): very rare — roughly End-city-tier rarity. Finding one is meant to feel like a genuine discovery, not a routine encounter during exploration.

**Void Sky Stone**
- Passive bonus: +1 channel per block placed adjacent to network cable/controller (flat, per-block, wired — not radius/aura, not controller-multiblock-only).
- Minor per-block but real at scale — a wall of it is a legitimate late-game min-max play without trivializing channel management or replacing dense cable outright.

## Structures

**Minor structures (mid-tier islands)** — small family, 4 variants:
- Server rack ruins — as in brief; light loot (sky stone, quartz, occasional processor).
- Downed drone wreck — combat-lite, acts as a Fragment spawn nest.
- Crashed cable-spool husk — light loot, mostly sky stone/quartz, low combat.
- Processor silo — best minor-structure loot, rarer than the other three, guarded by more Fragments.

**Capstone structure**
- "The Fractured Core" — boss-guarded, rare large ("motherboard") island only. Guaranteed flawless budding certus cluster + a unique precursor item feeding AE2's Singularity crafting chain. (Item name/mechanic still open.)

## Mobs — Fragments

**Combat roles**
- Mixed roles: at least two Fragment variants — a melee type and a ranged/projectile type — rather than a single archetype.

**Visual design**
- Small floating cable-cluster: a compact tangle of broken cable segments and quartz shards that hovers/drifts. No clear limbs/biped silhouette — reads as non-humanoid, an alien "glitch" rather than a corrupted golem. Both melee and ranged variants share this base silhouette.

**Channel-drain mechanic**
- Proximity debuff on the player: while a Fragment is alive within radius, the player's wireless terminal range and/or personal channel access is reduced.
- Implemented as a MobEffect on the player — no direct manipulation of real `IGridNode` state (keeps it decoupled from AE2 internals, avoids the tight-coupling maintenance burden).
- Radius: ~16 blocks per Fragment (room/structure-scale) — a nest debuffs the whole immediate area, pressuring players to clear fast rather than pick fights one at a time.
- Stacking: capped (e.g. ~3-4 Fragments' worth, exact number TBD at tuning time) — a nest is dangerous but proximity count alone can't fully zero out a player's network access.

## Boss — Fractured Core

**Identity**
- Twilight Forest Lich-style: normal humanoid scale (not a giant/mega-model), high HP, multiple attack phases rather than one repeated pattern. Not a Fragment-swarm/escort-wave fight.

**Visual design**
- Corrupted AE2 "operator" figure: humanoid, assembled from higher-tier AE2 materials (fluix/certus/processor plating) rather than the cable scrap Fragments are made of — visually a clear step up in material tier from regular Fragments, reading as "what a Fragment becomes at full power" rather than just a bigger Fragment.

**Attack phases**
- Teleport/blink around the arena (mobility phase, Lich-style).
- Summons waves of regular Fragments as adds during a phase.
- Channel-severing attack: periodically severs ALL player network access (terminals, wireless tools) within the arena radius for a short window — a real "fight without your network" phase, not a partial/soft debuff.

## Fractured Core Precursor Item

**Singularity Seed**
- Guaranteed drop from the Fractured Core boss.
- Drop-in substitute in AE2's existing Singularity crafting recipe (slots in place of/alongside the current ingredient) rather than starting a separate parallel recipe branch.

## Open (not yet locked)
- Exact tuning numbers: Fragment drain stacking cap/magnitude, boss health, phase sequencing/triggers
