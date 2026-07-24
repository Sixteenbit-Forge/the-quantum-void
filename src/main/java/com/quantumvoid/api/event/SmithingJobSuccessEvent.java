package com.quantumvoid.api.event;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;

/** Fired when a Quantum Smithing Table job succeeds. See {@link SmithingJobStartEvent}. */
public class SmithingJobSuccessEvent extends Event {
    private final Player player;
    private final ItemStack result;
    private final Identifier tier;

    public SmithingJobSuccessEvent(Player player, ItemStack result, Identifier tier) {
        this.player = player;
        this.result = result;
        this.tier = tier;
    }

    public Player player() {
        return player;
    }

    public ItemStack result() {
        return result;
    }

    public Identifier tier() {
        return tier;
    }

    public static void post(Player player, ItemStack result, Identifier tier) {
        NeoForge.EVENT_BUS.post(new SmithingJobSuccessEvent(player, result, tier));
    }
}
