package com.quantumvoid.api.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;

import java.util.List;

/** Fired when a Quantum Fuser (paxel merge) job completes successfully. */
public class FuserMergeCompleteEvent extends Event {
    private final Player player;
    private final List<ItemStack> inputs;
    private final ItemStack result;

    public FuserMergeCompleteEvent(Player player, List<ItemStack> inputs, ItemStack result) {
        this.player = player;
        this.inputs = List.copyOf(inputs);
        this.result = result;
    }

    public Player player() {
        return player;
    }

    public List<ItemStack> inputs() {
        return inputs;
    }

    public ItemStack result() {
        return result;
    }

    public static void post(Player player, List<ItemStack> inputs, ItemStack result) {
        NeoForge.EVENT_BUS.post(new FuserMergeCompleteEvent(player, inputs, result));
    }
}
