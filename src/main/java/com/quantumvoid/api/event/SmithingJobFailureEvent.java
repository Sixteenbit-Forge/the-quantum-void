package com.quantumvoid.api.event;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Fired when a Quantum Smithing Table job fails (network didn't have enough power available
 * the instant the job started). See {@link SmithingJobStartEvent}.
 */
public class SmithingJobFailureEvent extends Event {
    private final Player player;
    private final ItemStack input;
    private final Identifier tier;

    public SmithingJobFailureEvent(Player player, ItemStack input, Identifier tier) {
        this.player = player;
        this.input = input;
        this.tier = tier;
    }

    public Player player() {
        return player;
    }

    public ItemStack input() {
        return input;
    }

    public Identifier tier() {
        return tier;
    }

    public static void post(Player player, ItemStack input, Identifier tier) {
        NeoForge.EVENT_BUS.post(new SmithingJobFailureEvent(player, input, tier));
    }
}
