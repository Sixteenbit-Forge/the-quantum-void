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

**Implemented — Quantum Pearl linking (recipe corrected from the original lock-in)**
- "Quantum Entangled Singularities" was never a real item — the actual recipe implemented is
  a custom `QuantumPearlPairingRecipe` (2x `minecraft:ender_pearl` + 1x `ae2:fluix_crystal` →
  2x Quantum Pearl), a `CustomRecipe` rather than a plain shaped/shapeless JSON recipe because
  the result needs a freshly-generated random link ID baked in at craft time — both output
  pearls share it automatically since they're the same stack (count 2). A plain crafting-table
  recipe rather than the brief's "network-connected crafting device with an active, powered
  Quantum Ring formation" — a deliberate scope-down, that multiblock/network-gated crafting
  requirement isn't built.
- The link ID itself is a plain `long` data component (`QuantumComponents.PEARL_LINK`, 0 =
  unlinked/generic filler pearl). `QuantumPortalFrameBlock` reads whichever pearl completes the
  ring (the last one placed, not all 8 — the other 7 frame slots accept any Quantum Pearl,
  linked or not) and threads that link ID through to `QuantumPortalBlock`, which now implements
  `EntityBlock` purely to carry a `QuantumPortalBlockEntity` remembering its own link ID.
- `QuantumPortalLinkRegistry` maps a link ID to the most recently completed portal carrying it
  (dimension + position). When a linked portal's interior block is entered, it looks itself up
  in the registry for a different location than itself and teleports there directly instead of
  falling back to the existing fixed-direction/randomized-landing logic — which still runs
  unchanged whenever there's no link (an unlinked pearl-filled frame, or no linked destination
  registered yet).
- **In-memory only — does not persist across a server restart.** A real `SavedData`-backed
  registry (this MC version's codec-based `SavedDataType`) would fix that; skipped for scope,
  a real gap, not an oversight.

**Portal recolor — cyan/fluix, not vanilla purple**
- `QuantumPortalBlock` was a plain solid-cube `Block` with no ambient particles at all. Its
  `MapColor.COLOR_CYAN` (map-item tint) already signaled cyan intent even though the actual
  block texture was purple/violet up to this point — an inconsistency, now resolved.
- A texture recolor alone isn't enough to change the ambient particle color: vanilla's own
  portal particles get their purple from `ParticleTypes.PORTAL`'s built-in random purple-ish
  tint, so overriding it requires a distinct particle type whose provider calls
  `setColor(r, g, b)` on each spawned particle, not just a different block texture.
- `quantum_portal.png` hue-shifted (HSV rotate, ~275°→~187°) from purple to cyan. Added a new
  `quantumvoid:quantum_portal` `SimpleParticleType` (`QuantumVoid.PARTICLE_TYPES`), a
  client-only `QuantumPortalParticle` (`client` package, extends vanilla `PortalParticle`,
  `setColor(0.15F, 0.85F, 0.9F)`) registered via `RegisterParticleProvidersEvent` in
  `ClientSetup` (same `@EventBusSubscriber(value = Dist.CLIENT)` pattern already used for entity
  renderers), and an `animateTick` override on `QuantumPortalBlock` spawning 4 particles/tick at
  random positions in the block's volume (no frame-plane to hug, since this block is a full
  cube, not a thin directional slab). The particle sprite itself is reused directly from
  vanilla (`assets/quantumvoid/particles/quantum_portal.json` → the same `minecraft:generic_0..7`
  soft-glow sprite set vanilla's own portal particles use) — only the color is ours, no new
  sprite asset needed.
- Verified via `compileJava` plus live `runServer`/`runClient` boots — particle atlas builds
  clean, particle provider subscribes correctly, no missing-resource warnings.

## World Generation

**Chunk generator — CORRECTED from initial lock-in**
- ~~Custom `ChunkGenerator` subclass, hardcoded noise~~ — wrong on two counts, found by checking actual 26.1.2 data: (1) vanilla's own End dimension is generated entirely via the datapack density-function pipeline (`"type": "minecraft:noise"`, `"settings": "minecraft:end"`) in this version, not hardcoded Java; (2) no working example of a hand-written `ChunkGenerator` exists to verify signatures against (checked NeoForge's own repo and tests — none).
- Implementation: `data/quantumvoid/worldgen/noise_settings/quantum_void.json` — `"type": "minecraft:noise"` generator, `default_block`/`surface_rule` both set to `ae2:sky_stone_block`. Confirmed working via `gradlew runServer` — dimension loads and ticks with no schema errors, and confirmed in-game (real terrain, jagged island edges, no crashes).
- **Field-name pitfall, worth remembering**: misode/mcmeta's `summary` branch (a friendly export of resolved registry data) uses different field names than our actual 26.1.2 codec in several places — `input` vs. the codec's `argument`/`argument1`/`argument2` for density functions, `material_rule` vs. the codec's `surface_rule`. Cross-checking against another real, working dimension config, or triggering a deliberate validation error (which echoes the exact expected key name), was more reliable than trusting the summary export's field names. The `data` branch (raw per-file vanilla JSON) was closer but still wasn't 100% consistent — `"minecraft:add"` there used `left`/`right`, while our codec wants `argument1`/`argument2` (confirmed by the runtime error).
- `final_density`: rebuilt vanilla's `minecraft:end/sloped_cheese` manually (`add(end_islands, "minecraft:end/base_3d_noise")`) as `data/quantumvoid/worldgen/density_function/final_density.json`, referenced from the noise_router by string ID — router density-function slots only accept a number or a string reference to a separately-registered density_function resource, not an inlined compound object.
- Biome: **do not** reuse `minecraft:the_end` as the biome for a custom End-like dimension — it has `minecraft:end_spike` (obsidian pillars) and `minecraft:end_platform` (the exit portal) baked into its feature list, both tied to world origin, independent of `has_ender_dragon_fight`. Hit this exact bug (an exit-portal-looking block and a spike generated near spawn) and fixed it by adding our own `data/quantumvoid/worldgen/biome/quantum_void.json` with an empty feature list, referenced from the dimension's `biome_source` instead.
- Sky/fog color: also data-driven in this version, via a `dimension_type` JSON `attributes` map (`minecraft:visual/sky_color`, `sky_light_color`, `ambient_light_color`, `fog_color`) plus a top-level `skybox` field (`"end"` reuses the starfield-less End-style renderer). `sky_color` is the actual dome color; `sky_light_color`/`ambient_light_color` only tint lighting on blocks/terrain, not the dome itself — confirmed by testing (setting only the light-color attributes tinted the ground but left the sky pure black). Confirmed via vanilla's real `the_end` dimension_type (fetched from misode/mcmeta's `summary` branch, which — unlike for density functions — matched our codec's attribute field names correctly here).
- `final_density` rebuilt again: real floating terrain with varied peaks and gradual (not cliff-like) tapering, via a dual height-taper (density narrows both near the base and near the top) wrapped around `quantumvoid:base_3d_noise`, `data/quantumvoid/worldgen/density_function/final_density.json`. Naturally produces multiple separated islands without needing a separate placement layer.
- ~~Island tier shaping (small vs. rare "motherboard") not yet tuned.~~ Implemented — see "Island tiers" below.
- Portal arrival: destination column is randomized within a wide range rather than fixed, landing search widened and requires flat clearance in all four directions before accepting a spot (no more edge-of-cliff spawns), with a guaranteed-solid fallback point if the random search comes up empty.
- **Reactive fog near the Fractured Core**: the static `fog_color` above is the dimension's
  ambient default, but `QuantumFogEffects` (client-only, hooks NeoForge's
  `ViewportEvent.ComputeFogColor`) blends the fog toward a warning-red tint the closer the
  camera gets to a live `FracturedCoreEntity`, within a 64-block radius, fully back to the
  ambient default outside it. No dimension-level Java class exists for this in this MC version
  the way older per-dimension "special effects" registration classes historically worked — sky/
  fog here is otherwise entirely the data-driven `dimension_type` attributes above; this is a
  client-side rendering-event override layered on top, not a replacement for that system.

**Budding certus quartz**
- Two tiers: common non-flawless budding clusters scatter naturally on islands (ambient resource, no exploration gate); guaranteed flawless cluster reserved for structure loot (minor ruins + guaranteed at the Fractured Core).
- Implemented: reuses AE2's own `ae2:flawed_budding_quartz` block directly (rather than a new block) via an ore-style scatter feature, `data/quantumvoid/worldgen/{configured_feature,placed_feature}/flawed_budding_quartz.json`. Flawless tier still pending — reserved for structures, which don't exist yet.

**Wild fluix crystal growths**
- Two tiers: the glowing fluix veins running through the void (Section 3 visual identity) are themselves harvestable — the "circuit board" look doubles as ambient resource, not pure decoration. Denser/higher-yield growths cluster on and around structures as a farming incentive.

**Island tiers**
- Small islands: common, minor resources only.
- "Motherboard" islands (host structures): very rare — roughly End-city-tier rarity. Finding one is meant to feel like a genuine discovery, not a routine encounter during exploration.
- Implemented: a second biome (`quantumvoid:quantum_void_motherboard`) selected via `minecraft:multi_noise` biome source, gated on a dedicated low-frequency, large-wavelength density function (`motherboard_gate`) repurposing the otherwise-unused `erosion` noise router slot — rare by construction (narrow high-value band on a coarse noise). The same gate additively boosts `final_density` in those zones (bigger/taller terrain) and the motherboard biome gets denser ore/quartz placed-feature variants (`*_dense`). Exact rarity/size numbers are a first pass, not yet live-tuned.

**Void Sky Stone**
- ~~Passive +1 channel per adjacent block~~ — **superseded**: AE2's public `appeng.api.networking.pathing.IPathingService` is read-only (no hook to inject bonus channel capacity), and channels are fundamentally a cable-topology property, not a per-block flag, so the original mechanic doesn't map onto AE2's model at all.
- Revised: Void Sky Stone is an alternate cable block — it hosts a grid node and carries channels itself (like AE2's own dense cable), via the same public cable-hosting API (`IInWorldGridNodeHost`, `GridHelper`) AE2 uses internally for `CableBusBlock`. A stylish structural cable variant, not a stacking bonus.
- Implemented: `VoidSkyStoneBlockEntity` implements `IInWorldGridNodeHost` directly (not AE2's `appeng.blockentity.grid.AENetworkedBlockEntity` convenience base — that class lives outside the published `:api` jar this mod compiles against, confirmed by diffing the api jar's contents against the full mod jar). Node created/destroyed via `clearRemoved`/`setRemoved`, configured with `GridFlags.DENSE_CAPACITY`, exposed on all 6 sides. Crafted from 1x `ae2:sky_stone_block` + 1x `ae2:fluix_crystal` → 2x.
- **Bug found and fixed** (while building the ore-tools addon's own network-connected machines, which surfaced the same pattern): implementing `IInWorldGridNodeHost` is not enough on its own — `appeng.api.networking.GridHelper#getNodeHost` finds hosts via `level.getCapability(AECapabilities.IN_WORLD_GRID_NODE_HOST, pos, null)`, a block-position *capability* lookup, not an `instanceof` scan. Without also calling `event.registerBlockEntity(AECapabilities.IN_WORLD_GRID_NODE_HOST, VOID_SKY_STONE_BLOCK_ENTITY.get(), (be, unused) -> be)` in a `RegisterCapabilitiesEvent` listener, adjacent AE2 cables could never actually discover or connect to it — the block would sit inert despite the interface being fully implemented. Fixed in `QuantumVoid.java`'s constructor.

**Terrain block family**
- Void Dirt / Void Grass / Void Sand / Void Log / Void Leaves / Void Diamond Block / Void Emerald Block — purely aesthetic for now, no special mechanics (growth, spread, crafting uses) locked in yet; that's a later pass once the resource-farming loop is designed.
- Surface layering: Void Grass on top, Void Dirt for the next few blocks down, Sky Stone underneath (standard floor/depth surface rule).
- Void Diamond/Emerald Block scatter as rare ore-style deposits within Sky Stone, not mined ores — they're already the final block.
- Void trees scatter sparsely on Void Grass (straight trunk, blob foliage — plain aesthetic placeholder, no fruit/drop behavior yet).

## Structures

**Implementation note — Feature-based, not the vanilla Structure/StructurePiece pipeline**
- All 5 structures below are implemented as custom world-gen `Feature`s (`com.quantumvoid.structure.*`, extending a shared `AbstractRuinFeature` with `fillBox`/`hollowBox`/`placeLootChest`/`spawnGuard` helpers), placed via ordinary `placed_feature` + biome-features-array wiring — the same proven mechanism already used for ore/quartz/tree scatter — rather than the full vanilla `Structure`/`StructureType`/`StructurePiece`/`StructureSet` pipeline. Same "find a small ruin with loot and guards" player-facing outcome; deliberately lower-risk given no NBT structure templates could be hand-authored without in-game structure-block access, and the full Structure pipeline is one of the most failure-prone corners of data-driven world-gen. Revisit as real hand-built NBT templates + proper Structure registration if/when the rooms need to be less proc-gen-boxy.

**Minor structures (mid-tier islands)** — small family, 4 variants, all on the regular `quantum_void` biome via `SURFACE_STRUCTURES` feature slot, rarity via `minecraft:rarity_filter`:
- Server rack ruins — light loot (sky stone, quartz, occasional Void Diamond Block), no guards. `chance: 30`.
- Downed drone wreck — combat-lite, 2 Fragment guards (1 melee, 1 ranged). `chance: 35`.
- Crashed cable-spool husk — light loot, 50% chance of 1 guard. `chance: 35`.
- Processor silo — best minor-structure loot (2 chests), rarer than the other three, guarded by 3 Fragments. `chance: 60`.

**Capstone structure**
- "The Fractured Core" — boss-guarded, motherboard-island only (`quantum_void_motherboard` biome, `chance: 6` within that already-rare biome). Places a guaranteed `ae2:flawless_budding_quartz` block and spawns the Fractured Core boss directly. The precursor item is the Singularity Seed (guaranteed boss drop, not structure loot — see below).

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
- Implemented: `ChannelDrainEffect` (`quantumvoid:channel_drain`) applies a movement-speed/attack-speed penalty via `MobEffect.addAttributeModifier`. The actual "reduces wireless terminal range" hook into AE2 is still open (no public API found for it, matching the design note above about `IPathingService` being read-only) — the attribute penalty stands in as the "your network access is compromised" feel for now.
- Fragment entities (`FragmentMeleeEntity`/`FragmentRangedEntity`) extend a shared `AbstractFragmentEntity extends Monster` using `FlyingMoveControl`/`FlyingPathNavigation` (same pattern as vanilla Bees); visually reuse vanilla's `EndermiteModel` geometry (small, segmented, already non-humanoid) with a custom texture rather than a hand-built model — ranged variant fires `FragmentBoltEntity` (a `ThrowableItemProjectile` reusing the Quantum Pearl icon, rendered via vanilla's generic `ThrownItemRenderer`, no new projectile model needed).

**Fragment Cocoon — a growth-stage spawner block**
- `FragmentCocoonBlock` visibly progresses through 5 `stage` block-state values (like a turtle
  egg) via `randomTick`, each stage swapping to a distinct model/texture with more visible
  glowing cracks. On reaching the final stage, the next successful random tick removes the
  block and spawns a real Fragment (melee or ranged, chosen randomly) in its place instead of
  advancing further — the same `snapTo`/`finalizeSpawn`/`addFreshEntity` sequence structures
  already use for guard spawns. Requires a sturdy floor beneath it, same support check as the
  crystal-style blocks elsewhere in the addon; not placed by world-gen yet, hand/creative only.

## Boss — Fractured Core

**Identity**
- Twilight Forest Lich-style: normal humanoid scale (not a giant/mega-model), high HP, multiple attack phases rather than one repeated pattern. Not a Fragment-swarm/escort-wave fight.

**Visual design**
- Corrupted AE2 "operator" figure: humanoid, assembled from higher-tier AE2 materials (fluix/certus/processor plating) rather than the cable scrap Fragments are made of — visually a clear step up in material tier from regular Fragments, reading as "what a Fragment becomes at full power" rather than just a bigger Fragment.

**Attack phases**
- Teleport/blink around the arena (mobility phase, Lich-style).
- Summons waves of regular Fragments as adds during a phase.
- Channel-severing attack: periodically severs ALL player network access (terminals, wireless tools) within the arena radius for a short window — a real "fight without your network" phase, not a partial/soft debuff.
- Implemented: `FracturedCoreEntity extends Monster` with a `ServerBossEvent` boss bar (purple, progress overlay), 150 HP. Each phase is its own no-interrupt `Goal` (`BlinkGoal`, `SummonFragmentsGoal`, `ChannelSeverGoal`) on independent cooldowns alongside a baseline `MeleeAttackGoal`, so the fight naturally varies rather than following one scripted sequence. Visually reuses vanilla's generic `HumanoidModel`/`HumanoidMobRenderer` (the same reusable base `PillagerRenderer`/`SkeletonRenderer` etc. build on) with a custom texture, rather than a hand-built model.

## Fractured Core Precursor Item

**Singularity Seed**
- Guaranteed drop from the Fractured Core boss.
- ~~Drop-in substitute in AE2's existing Singularity crafting recipe~~ — **superseded**: checked AE2's real recipe data directly; `ae2:singularity` has no data-driven recipe at all to slot into (it's the hardcoded Matter Cannon "condense 256 matter balls" mechanic, confirmed by the total absence of any `recipe/*.json` outputting it). Patching that would mean touching AE2's internal Java, not a safe datapack change.
- Implemented: Singularity Seed unlocks its own shapeless recipe (1x seed + 8x `ae2:fluix_crystal` → 1x `ae2:singularity`) — a real alternate path to the same output, achieving the "boss-gated shortcut to Singularities" intent without touching AE2 internals.

## Addon API

The mod exposes a small, stable `com.quantumvoid.api` package for external addons to build against, so addon mods (e.g. an ore/tool progression pack) don't need to depend on this mod's internals. Anything in this package is an additive-only compatibility promise once an addon depends on a version.

- `api.ore.FractureCoreOreRegistry` — addons call `register(OreEntry)` once at mod-init to add ore blocks to the Fractured Core dimension's world-gen, no editing of this mod's biome/feature files required. Backed by `RegistryDrivenOreFeature`, a Feature already wired into both biomes' ore slot that reads the registry at placement time and delegates to vanilla's real `Feature.ORE` per entry (proper veins, not single blocks). Empty registry today — a genuine no-op until an addon registers something.
- `api.tags.QuantumVoidTags` — tier-gating block tags (`needs_charged_certus_tier`/`needs_fluxstone_tier`/`needs_quantumite_tier`/`needs_paradoxium_tier`), mirroring vanilla's own `needs_iron_tool`/`needs_diamond_tool` convention. Data-driven on purpose — a third-party addon can slot a new tier into the same chain purely through datapack tags, no code dependency.
- `api.event` — `SmithingJobStartEvent`/`SuccessEvent`/`FailureEvent`, `FuserMergeCompleteEvent`, `TierUpgradeAppliedEvent`, `QuantumPortalTravelEvent`. Tiers/upgrades are identified by `Identifier`, not an enum, so new ones don't require a change here. `QuantumPortalTravelEvent` is the one this mod fires itself (wired into the real portal teleport code) — the others are event *definitions* for whichever mod implements the Smithing Table/Fuser to fire.
- `api.upgrade.IQuantumUpgradeHolder` — open-ended upgrade-slot interface (installed upgrades identified by `Identifier`, not a fixed enum) plus a shared `ItemCapability` constant (`QuantumUpgradeCapabilities.UPGRADE_HOLDER`) any mod's items can register against.
- `api.QuantumVoidApi.singularitySeed()` — the sanctioned way to reference this mod's Singularity Seed item; addons should consume it through here rather than reaching into `QuantumVoid.SINGULARITY_SEED` directly.

## Open (not yet locked)
- Exact tuning numbers: Fragment drain stacking cap/magnitude, boss health, phase sequencing/triggers
- Motherboard-biome rarity/size numbers — first-pass placeholder, not yet observed in a live world
- Real AE2 hook for reducing wireless terminal range (channel-drain currently uses a generic speed/attack-speed penalty instead)
- `QuantumPortalLinkRegistry` doesn't persist across a server restart (in-memory only)
- No world-gen placement for Fragment Cocoon — hand/creative only
- Client-side play-through of the corrected Quantum Pearl pairing recipe and portal-linking
  flow (craft a pair, build both frames, confirm the link actually routes correctly) is
  unverified beyond compile/boot — only the fallback (unlinked) portal path has been used
  live so far.
