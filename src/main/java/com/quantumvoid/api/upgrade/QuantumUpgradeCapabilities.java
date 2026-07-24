package com.quantumvoid.api.upgrade;

import com.quantumvoid.QuantumVoid;

import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.capabilities.ItemCapability;

/**
 * The shared {@link IQuantumUpgradeHolder} capability instance. Register your own upgradeable
 * items against this constant during {@code RegisterCapabilitiesEvent} (a NeoForge mod-bus
 * event) — the base mod doesn't register any items against it itself, since it has none of
 * its own upgradeable gear; this only declares the shared capability so every mod that cares
 * refers to the exact same instance.
 * <p>
 * Stable API surface — additive changes only once an addon depends on a version.
 */
public final class QuantumUpgradeCapabilities {
    private QuantumUpgradeCapabilities() {
    }

    public static final ItemCapability<IQuantumUpgradeHolder, Void> UPGRADE_HOLDER =
            ItemCapability.createVoid(Identifier.fromNamespaceAndPath(QuantumVoid.MODID, "upgrade_holder"), IQuantumUpgradeHolder.class);
}
