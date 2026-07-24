package com.quantumvoid.api.event;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Fired when an AE2-flavored upgrade module (Portability Module, Wireless Access Core, etc.)
 * is applied to a piece of gear at the Quantum Smithing Table. {@code upgradeId} identifies
 * the upgrade the same data-driven way {@code tier} identifies tiers elsewhere in this
 * package — see {@link com.quantumvoid.api.upgrade.IQuantumUpgradeHolder}.
 */
public class TierUpgradeAppliedEvent extends Event {
    private final Player player;
    private final ItemStack gear;
    private final Identifier upgradeId;

    public TierUpgradeAppliedEvent(Player player, ItemStack gear, Identifier upgradeId) {
        this.player = player;
        this.gear = gear;
        this.upgradeId = upgradeId;
    }

    public Player player() {
        return player;
    }

    public ItemStack gear() {
        return gear;
    }

    public Identifier upgradeId() {
        return upgradeId;
    }

    public static void post(Player player, ItemStack gear, Identifier upgradeId) {
        NeoForge.EVENT_BUS.post(new TierUpgradeAppliedEvent(player, gear, upgradeId));
    }
}
