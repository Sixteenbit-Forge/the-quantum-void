package com.quantumvoid;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.quantumvoid.block.QuantumPortalBlock;
import com.quantumvoid.block.QuantumPortalFrameBlock;
import com.quantumvoid.block.VoidLeavesBlock;
import com.quantumvoid.block.VoidSandBlock;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
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

    // Phase 0: consumed by a Quantum Portal Frame to activate it. Linking (pearl pairing)
    // is not implemented yet — activation currently teleports to a fixed destination.
    public static final DeferredItem<Item> QUANTUM_PEARL = ITEMS.registerSimpleItem("quantum_pearl", p -> p);

    public static final DeferredBlock<QuantumPortalFrameBlock> QUANTUM_PORTAL_FRAME = BLOCKS.registerBlock("quantum_portal_frame",
            QuantumPortalFrameBlock::new,
            p -> p.mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .requiresCorrectToolForDrops()
                    .strength(50.0f, 1200.0f)
                    .lightLevel(state -> 4));
    public static final DeferredItem<BlockItem> QUANTUM_PORTAL_FRAME_ITEM = ITEMS.registerSimpleBlockItem("quantum_portal_frame", QUANTUM_PORTAL_FRAME);

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

    public static final DeferredBlock<Block> VOID_DIAMOND_BLOCK = BLOCKS.registerBlock("void_diamond_block",
            Block::new, p -> p.mapColor(MapColor.DIAMOND).requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<BlockItem> VOID_DIAMOND_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("void_diamond_block", VOID_DIAMOND_BLOCK);

    public static final DeferredBlock<Block> VOID_EMERALD_BLOCK = BLOCKS.registerBlock("void_emerald_block",
            Block::new, p -> p.mapColor(MapColor.EMERALD).requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<BlockItem> VOID_EMERALD_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("void_emerald_block", VOID_EMERALD_BLOCK);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> QUANTUM_VOID_TAB = CREATIVE_MODE_TABS.register("quantum_void_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.quantumvoid"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> QUANTUM_PEARL.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(QUANTUM_PEARL.get());
                        output.accept(QUANTUM_PORTAL_FRAME_ITEM.get());
                        output.accept(VOID_DIRT_ITEM.get());
                        output.accept(VOID_GRASS_ITEM.get());
                        output.accept(VOID_SAND_ITEM.get());
                        output.accept(VOID_LOG_ITEM.get());
                        output.accept(VOID_LEAVES_ITEM.get());
                        output.accept(VOID_DIAMOND_BLOCK_ITEM.get());
                        output.accept(VOID_EMERALD_BLOCK_ITEM.get());
                    }).build());

    public QuantumVoid(IEventBus modEventBus, ModContainer modContainer) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}
