package com.quantumvoid.api.event;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Fired when a Quantum Smithing Table (or equivalent) begins a tier-upgrade job, before the
 * network power draw resolves. {@code tier} is a data-driven identifier (e.g.
 * {@code addonid:fluxstone}), not an enum — any addon can introduce new tiers without this
 * event needing to change.
 * <p>
 * Stable API surface — additive changes only once an addon depends on a version.
 */
public class SmithingJobStartEvent extends Event {
    private final Player player;
    private final ItemStack input;
    private final Identifier tier;

    public SmithingJobStartEvent(Player player, ItemStack input, Identifier tier) {
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
        NeoForge.EVENT_BUS.post(new SmithingJobStartEvent(player, input, tier));
    }
}
