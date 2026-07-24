package com.quantumvoid;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.quantumvoid.block.QuantumPortalBlock;
import com.quantumvoid.block.QuantumPortalFrameBlock;
import com.quantumvoid.block.VoidLeavesBlock;
import com.quantumvoid.block.VoidSandBlock;
import com.quantumvoid.block.VoidSkyStoneBlock;
import com.quantumvoid.block.VoidSkyStoneBlockEntity;
import com.quantumvoid.boss.FracturedCoreEntity;
import com.quantumvoid.effect.ChannelDrainEffect;
import com.quantumvoid.entity.AbstractFragmentEntity;
import com.quantumvoid.entity.FragmentBoltEntity;
import com.quantumvoid.entity.FragmentMeleeEntity;
import com.quantumvoid.entity.FragmentRangedEntity;
import com.quantumvoid.structure.CableSpoolHuskFeature;
import com.quantumvoid.structure.DroneWreckFeature;
import com.quantumvoid.structure.FracturedCoreFeature;
import com.quantumvoid.structure.ProcessorSiloFeature;
import com.quantumvoid.structure.ServerRackRuinsFeature;
import com.quantumvoid.worldgen.RegistryDrivenOreFeature;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(QuantumVoid.MODID)
public class QuantumVoid {
    public static final String MODID = "quantumvoid";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, MODID);
    public static final DeferredRegister<net.minecraft.core.particles.ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, MODID);

    private static ResourceKey<EntityType<?>> entityKey(String name) {
        return ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(MODID, name));
    }

    public static final DeferredHolder<EntityType<?>, EntityType<FragmentMeleeEntity>> FRAGMENT_MELEE = ENTITY_TYPES.register("fragment_melee",
            () -> EntityType.Builder.of(FragmentMeleeEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 0.6f)
                    .clientTrackingRange(8)
                    .build(entityKey("fragment_melee")));

    public static final DeferredHolder<EntityType<?>, EntityType<FragmentRangedEntity>> FRAGMENT_RANGED = ENTITY_TYPES.register("fragment_ranged",
            () -> EntityType.Builder.of(FragmentRangedEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 0.6f)
                    .clientTrackingRange(8)
                    .build(entityKey("fragment_ranged")));

    public static final DeferredHolder<EntityType<?>, EntityType<FragmentBoltEntity>> FRAGMENT_BOLT = ENTITY_TYPES.register("fragment_bolt",
            () -> EntityType.Builder.<FragmentBoltEntity>of(FragmentBoltEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.25f, 0.25f)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(entityKey("fragment_bolt")));

    public static final DeferredHolder<EntityType<?>, EntityType<FracturedCoreEntity>> FRACTURED_CORE = ENTITY_TYPES.register("fractured_core",
            () -> EntityType.Builder.of(FracturedCoreEntity::new, MobCategory.MONSTER)
                    .sized(0.8f, 2.2f)
                    .clientTrackingRange(16)
                    .fireImmune()
                    .build(entityKey("fractured_core")));

    public static final DeferredHolder<MobEffect, MobEffect> CHANNEL_DRAIN = MOB_EFFECTS.register("channel_drain", ChannelDrainEffect::new);

    public static final DeferredRegister<net.minecraft.world.level.levelgen.feature.Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, MODID);
    public static final DeferredHolder<net.minecraft.world.level.levelgen.feature.Feature<?>, ServerRackRuinsFeature> SERVER_RACK_RUINS_FEATURE = FEATURES.register("server_rack_ruins", ServerRackRuinsFeature::new);
    public static final DeferredHolder<net.minecraft.world.level.levelgen.feature.Feature<?>, DroneWreckFeature> DRONE_WRECK_FEATURE = FEATURES.register("downed_drone_wreck", DroneWreckFeature::new);
    public static final DeferredHolder<net.minecraft.world.level.levelgen.feature.Feature<?>, CableSpoolHuskFeature> CABLE_SPOOL_HUSK_FEATURE = FEATURES.register("cable_spool_husk", CableSpoolHuskFeature::new);
    public static final DeferredHolder<net.minecraft.world.level.levelgen.feature.Feature<?>, ProcessorSiloFeature> PROCESSOR_SILO_FEATURE = FEATURES.register("processor_silo", ProcessorSiloFeature::new);
    public static final DeferredHolder<net.minecraft.world.level.levelgen.feature.Feature<?>, FracturedCoreFeature> FRACTURED_CORE_FEATURE = FEATURES.register("fractured_core", FracturedCoreFeature::new);

    // Addon ore world-gen — see docs/DESIGN.md and com.quantumvoid.api.ore.FractureCoreOreRegistry.
    public static final DeferredHolder<net.minecraft.world.level.levelgen.feature.Feature<?>, RegistryDrivenOreFeature> REGISTRY_DRIVEN_ORE_FEATURE =
            FEATURES.register("addon_ore", RegistryDrivenOreFeature::new);

    // Phase 0: consumed by a Quantum Portal Frame to activate it. Linking (pearl pairing)
    // is not implemented yet — activation currently teleports to a fixed destination.
    public static final DeferredItem<Item> QUANTUM_PEARL = ITEMS.registerSimpleItem("quantum_pearl", p -> p);

    // Guaranteed drop from the Fractured Core boss (see docs/DESIGN.md — "Fractured Core Precursor Item").
    // AE2's real ae2:singularity has no data-driven recipe to slot into (matter-cannon-only, hardcoded),
    // so this unlocks its own recipe to the same output rather than patching AE2 internals.
    public static final DeferredItem<Item> SINGULARITY_SEED = ITEMS.registerSimpleItem("singularity_seed", p -> p.rarity(net.minecraft.world.item.Rarity.EPIC));

    public static final DeferredBlock<QuantumPortalFrameBlock> QUANTUM_PORTAL_FRAME = BLOCKS.registerBlock("quantum_portal_frame",
            QuantumPortalFrameBlock::new,
            p -> p.mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .requiresCorrectToolForDrops()
                    .strength(50.0f, 1200.0f)
                    .lightLevel(state -> 4));
    public static final DeferredItem<BlockItem> QUANTUM_PORTAL_FRAME_ITEM = ITEMS.registerSimpleBlockItem("quantum_portal_frame", QUANTUM_PORTAL_FRAME);

    // Ambient particle tint for the portal — cyan/fluix rather than vanilla's purple, matching
    // the block's own MapColor.COLOR_CYAN below. See docs/DESIGN.md "Portal recolor".
    public static final DeferredHolder<net.minecraft.core.particles.ParticleType<?>, net.minecraft.core.particles.SimpleParticleType> QUANTUM_PORTAL_PARTICLE =
            PARTICLE_TYPES.register("quantum_portal", () -> new net.minecraft.core.particles.SimpleParticleType(false));

    // The interior "you're inside the portal now" block. Not directly obtainable — no BlockItem.
    public static final DeferredBlock<QuantumPortalBlock> QUANTUM_PORTAL = BLOCKS.registerBlock("quantum_portal",
            QuantumPortalBlock::new,
            p -> p.mapColor(MapColor.COLOR_CYAN)
                    .noCollision()
                    .noLootTable()
                    .lightLevel(state -> 11)
                    .sound(net.minecraft.world.level.block.SoundType.GLASS)
                    .pushReaction(PushReaction.BLOCK)
                    .strength(-1.0f, 3600000.0f));

    // Terrain block family. Purely aesthetic for now — mechanics (growth, spread, etc.) are a later pass.
    public static final DeferredBlock<Block> VOID_DIRT = BLOCKS.registerBlock("void_dirt",
            Block::new, p -> p.mapColor(MapColor.COLOR_PURPLE).strength(0.5f).sound(SoundType.GRAVEL));
    public static final DeferredItem<BlockItem> VOID_DIRT_ITEM = ITEMS.registerSimpleBlockItem("void_dirt", VOID_DIRT);

    public static final DeferredBlock<Block> VOID_GRASS = BLOCKS.registerBlock("void_grass",
            Block::new, p -> p.mapColor(MapColor.COLOR_MAGENTA).strength(0.6f).sound(SoundType.GRASS));
    public static final DeferredItem<BlockItem> VOID_GRASS_ITEM = ITEMS.registerSimpleBlockItem("void_grass", VOID_GRASS);

    public static final DeferredBlock<VoidSandBlock> VOID_SAND = BLOCKS.registerBlock("void_sand",
            VoidSandBlock::new, p -> p.mapColor(MapColor.COLOR_LIGHT_BLUE).strength(0.5f).sound(SoundType.SAND));
    public static final DeferredItem<BlockItem> VOID_SAND_ITEM = ITEMS.registerSimpleBlockItem("void_sand", VOID_SAND);

    public static final DeferredBlock<RotatedPillarBlock> VOID_LOG = BLOCKS.registerBlock("void_log",
            RotatedPillarBlock::new, p -> p.mapColor(MapColor.COLOR_PURPLE).strength(2.0f).sound(SoundType.WOOD));
    public static final DeferredItem<BlockItem> VOID_LOG_ITEM = ITEMS.registerSimpleBlockItem("void_log", VOID_LOG);

    public static final DeferredBlock<VoidLeavesBlock> VOID_LEAVES = BLOCKS.registerBlock("void_leaves",
            VoidLeavesBlock::new, p -> p.mapColor(MapColor.COLOR_MAGENTA).strength(0.2f).sound(SoundType.GRASS).noOcclusion());
    public static final DeferredItem<BlockItem> VOID_LEAVES_ITEM = ITEMS.registerSimpleBlockItem("void_leaves", VOID_LEAVES);

    // Void wood set — matches vanilla oak's core building block family. Signs and boats deferred.
    public static final DeferredBlock<Block> VOID_PLANKS = BLOCKS.registerBlock("void_planks",
            Block::new, p -> p.mapColor(MapColor.COLOR_PURPLE).strength(2.0f, 3.0f).sound(SoundType.WOOD));
    public static final DeferredItem<BlockItem> VOID_PLANKS_ITEM = ITEMS.registerSimpleBlockItem("void_planks", VOID_PLANKS);

    public static final DeferredBlock<RotatedPillarBlock> VOID_STRIPPED_LOG = BLOCKS.registerBlock("void_stripped_log",
            RotatedPillarBlock::new, p -> p.mapColor(MapColor.COLOR_PURPLE).strength(2.0f).sound(SoundType.WOOD));
    public static final DeferredItem<BlockItem> VOID_STRIPPED_LOG_ITEM = ITEMS.registerSimpleBlockItem("void_stripped_log", VOID_STRIPPED_LOG);

    public static final DeferredBlock<RotatedPillarBlock> VOID_WOOD = BLOCKS.registerBlock("void_wood",
            RotatedPillarBlock::new, p -> p.mapColor(MapColor.COLOR_PURPLE).strength(2.0f).sound(SoundType.WOOD));
    public static final DeferredItem<BlockItem> VOID_WOOD_ITEM = ITEMS.registerSimpleBlockItem("void_wood", VOID_WOOD);

    public static final DeferredBlock<RotatedPillarBlock> VOID_STRIPPED_WOOD = BLOCKS.registerBlock("void_stripped_wood",
            RotatedPillarBlock::new, p -> p.mapColor(MapColor.COLOR_PURPLE).strength(2.0f).sound(SoundType.WOOD));
    public static final DeferredItem<BlockItem> VOID_STRIPPED_WOOD_ITEM = ITEMS.registerSimpleBlockItem("void_stripped_wood", VOID_STRIPPED_WOOD);

    public static final DeferredBlock<StairBlock> VOID_STAIRS = BLOCKS.registerBlock("void_stairs",
            p -> new StairBlock(VOID_PLANKS.get().defaultBlockState(), p),
            p -> p.mapColor(MapColor.COLOR_PURPLE).strength(2.0f, 3.0f).sound(SoundType.WOOD));
    public static final DeferredItem<BlockItem> VOID_STAIRS_ITEM = ITEMS.registerSimpleBlockItem("void_stairs", VOID_STAIRS);

    public static final DeferredBlock<SlabBlock> VOID_SLAB = BLOCKS.registerBlock("void_slab",
            SlabBlock::new, p -> p.mapColor(MapColor.COLOR_PURPLE).strength(2.0f, 3.0f).sound(SoundType.WOOD));
    public static final DeferredItem<BlockItem> VOID_SLAB_ITEM = ITEMS.registerSimpleBlockItem("void_slab", VOID_SLAB);

    public static final DeferredBlock<FenceBlock> VOID_FENCE = BLOCKS.registerBlock("void_fence",
            FenceBlock::new, p -> p.mapColor(MapColor.COLOR_PURPLE).strength(2.0f, 3.0f).sound(SoundType.WOOD));
    public static final DeferredItem<BlockItem> VOID_FENCE_ITEM = ITEMS.registerSimpleBlockItem("void_fence", VOID_FENCE);

    public static final DeferredBlock<FenceGateBlock> VOID_FENCE_GATE = BLOCKS.registerBlock("void_fence_gate",
            p -> new FenceGateBlock(WoodType.OAK, p),
            p -> p.mapColor(MapColor.COLOR_PURPLE).strength(2.0f, 3.0f).sound(SoundType.WOOD));
    public static final DeferredItem<BlockItem> VOID_FENCE_GATE_ITEM = ITEMS.registerSimpleBlockItem("void_fence_gate", VOID_FENCE_GATE);

    public static final DeferredBlock<DoorBlock> VOID_DOOR = BLOCKS.registerBlock("void_door",
            p -> new DoorBlock(BlockSetType.OAK, p),
            p -> p.mapColor(MapColor.COLOR_PURPLE).strength(3.0f).sound(SoundType.WOOD).noOcclusion());
    public static final DeferredItem<BlockItem> VOID_DOOR_ITEM = ITEMS.registerSimpleBlockItem("void_door", VOID_DOOR);

    public static final DeferredBlock<TrapDoorBlock> VOID_TRAPDOOR = BLOCKS.registerBlock("void_trapdoor",
            p -> new TrapDoorBlock(BlockSetType.OAK, p),
            p -> p.mapColor(MapColor.COLOR_PURPLE).strength(3.0f).sound(SoundType.WOOD).noOcclusion());
    public static final DeferredItem<BlockItem> VOID_TRAPDOOR_ITEM = ITEMS.registerSimpleBlockItem("void_trapdoor", VOID_TRAPDOOR);

    public static final DeferredBlock<PressurePlateBlock> VOID_PRESSURE_PLATE = BLOCKS.registerBlock("void_pressure_plate",
            p -> new PressurePlateBlock(BlockSetType.OAK, p),
            p -> p.mapColor(MapColor.COLOR_PURPLE).strength(0.5f).sound(SoundType.WOOD).noCollision());
    public static final DeferredItem<BlockItem> VOID_PRESSURE_PLATE_ITEM = ITEMS.registerSimpleBlockItem("void_pressure_plate", VOID_PRESSURE_PLATE);

    public static final DeferredBlock<ButtonBlock> VOID_BUTTON = BLOCKS.registerBlock("void_button",
            p -> new ButtonBlock(BlockSetType.OAK, 30, p),
            p -> p.mapColor(MapColor.COLOR_PURPLE).strength(0.5f).sound(SoundType.WOOD).noCollision());
    public static final DeferredItem<BlockItem> VOID_BUTTON_ITEM = ITEMS.registerSimpleBlockItem("void_button", VOID_BUTTON);

    public static final DeferredBlock<Block> VOID_DIAMOND_BLOCK = BLOCKS.registerBlock("void_diamond_block",
            Block::new, p -> p.mapColor(MapColor.DIAMOND).requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<BlockItem> VOID_DIAMOND_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("void_diamond_block", VOID_DIAMOND_BLOCK);

    public static final DeferredBlock<Block> VOID_EMERALD_BLOCK = BLOCKS.registerBlock("void_emerald_block",
            Block::new, p -> p.mapColor(MapColor.EMERALD).requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<BlockItem> VOID_EMERALD_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("void_emerald_block", VOID_EMERALD_BLOCK);

    // Alternate cable block — hosts a grid node and carries channels itself (see docs/DESIGN.md — "Void Sky Stone").
    public static final DeferredBlock<VoidSkyStoneBlock> VOID_SKY_STONE = BLOCKS.registerBlock("void_sky_stone",
            VoidSkyStoneBlock::new, p -> p.mapColor(MapColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops().strength(3.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<BlockItem> VOID_SKY_STONE_ITEM = ITEMS.registerSimpleBlockItem("void_sky_stone", VOID_SKY_STONE);

    public static final DeferredRegister<net.minecraft.world.level.block.entity.BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    public static final DeferredHolder<net.minecraft.world.level.block.entity.BlockEntityType<?>, net.minecraft.world.level.block.entity.BlockEntityType<VoidSkyStoneBlockEntity>> VOID_SKY_STONE_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register("void_sky_stone", () -> new net.minecraft.world.level.block.entity.BlockEntityType<VoidSkyStoneBlockEntity>(VoidSkyStoneBlockEntity::new, VOID_SKY_STONE.get()));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> QUANTUM_VOID_TAB = CREATIVE_MODE_TABS.register("quantum_void_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.quantumvoid"))
                    .icon(() -> QUANTUM_PEARL.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(QUANTUM_PEARL.get());
                        output.accept(SINGULARITY_SEED.get());
                        output.accept(QUANTUM_PORTAL_FRAME_ITEM.get());
                        output.accept(VOID_DIRT_ITEM.get());
                        output.accept(VOID_GRASS_ITEM.get());
                        output.accept(VOID_SAND_ITEM.get());
                        output.accept(VOID_LOG_ITEM.get());
                        output.accept(VOID_LEAVES_ITEM.get());
                        output.accept(VOID_PLANKS_ITEM.get());
                        output.accept(VOID_STRIPPED_LOG_ITEM.get());
                        output.accept(VOID_WOOD_ITEM.get());
                        output.accept(VOID_STRIPPED_WOOD_ITEM.get());
                        output.accept(VOID_STAIRS_ITEM.get());
                        output.accept(VOID_SLAB_ITEM.get());
                        output.accept(VOID_FENCE_ITEM.get());
                        output.accept(VOID_FENCE_GATE_ITEM.get());
                        output.accept(VOID_DOOR_ITEM.get());
                        output.accept(VOID_TRAPDOOR_ITEM.get());
                        output.accept(VOID_PRESSURE_PLATE_ITEM.get());
                        output.accept(VOID_BUTTON_ITEM.get());
                        output.accept(VOID_DIAMOND_BLOCK_ITEM.get());
                        output.accept(VOID_EMERALD_BLOCK_ITEM.get());
                        output.accept(VOID_SKY_STONE_ITEM.get());
                    }).build());

    public QuantumVoid(IEventBus modEventBus, ModContainer modContainer) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        ENTITY_TYPES.register(modEventBus);
        MOB_EFFECTS.register(modEventBus);
        PARTICLE_TYPES.register(modEventBus);
        FEATURES.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);

        modEventBus.addListener(this::registerAttributes);
        modEventBus.addListener(this::registerCapabilities);
    }

    private void registerAttributes(net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent event) {
        event.put(FRAGMENT_MELEE.get(), AbstractFragmentEntity.createAttributes().build());
        event.put(FRAGMENT_RANGED.get(), AbstractFragmentEntity.createAttributes().build());
        event.put(FRACTURED_CORE.get(), FracturedCoreEntity.createAttributes().build());
    }

    private void registerCapabilities(net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent event) {
        // Needed for adjacent AE2 cables to actually find this block - GridHelper#getNodeHost
        // does a capability lookup, not an instanceof check.
        event.registerBlockEntity(appeng.api.AECapabilities.IN_WORLD_GRID_NODE_HOST, VOID_SKY_STONE_BLOCK_ENTITY.get(), (be, unused) -> be);
    }
}
