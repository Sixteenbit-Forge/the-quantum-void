package com.quantumvoid.api.upgrade;

import net.minecraft.resources.Identifier;

import java.util.Set;

/**
 * Exposes a tool/armor piece's installed AE2-flavored upgrade modules (Portability Module,
 * Wireless Access Core, Power Conduit, etc.) through an open-ended interface rather than a
 * fixed enum of upgrade types — an addon can introduce an entirely new upgrade that still
 * slots into the same Quantum Smithing Table UI without this mod needing to change.
 * <p>
 * Upgrades are identified by {@link Identifier} (e.g. {@code addonid:power_conduit}), the
 * same data-driven convention used across this API package. Whoever registers this
 * capability on their own items (see {@link QuantumUpgradeCapabilities#UPGRADE_HOLDER})
 * owns the actual storage/persistence — this interface only defines the read/write contract.
 * <p>
 * Stable API surface — additive changes only once an addon depends on a version.
 */
public interface IQuantumUpgradeHolder {
    Set<Identifier> installedUpgrades();

    boolean hasUpgrade(Identifier upgradeId);

    /** @return false if the upgrade was already installed or didn't fit (e.g. slot/tier limits). */
    boolean installUpgrade(Identifier upgradeId);

    /** @return false if the upgrade wasn't installed. */
    boolean removeUpgrade(Identifier upgradeId);

    int maxUpgradeSlots();
}
