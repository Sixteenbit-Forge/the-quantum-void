package com.quantumvoid;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.quantumvoid.block.QuantumPortalBlock;
import com.quantumvoid.block.QuantumPortalFrameBlock;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
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

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> QUANTUM_VOID_TAB = CREATIVE_MODE_TABS.register("quantum_void_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.quantumvoid"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> QUANTUM_PEARL.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(QUANTUM_PEARL.get());
                        output.accept(QUANTUM_PORTAL_FRAME_ITEM.get());
                    }).build());

    public QuantumVoid(IEventBus modEventBus, ModContainer modContainer) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}
