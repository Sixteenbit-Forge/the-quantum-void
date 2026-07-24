package com.quantumvoid.api;

import com.quantumvoid.QuantumVoid;

import net.minecraft.world.item.Item;

import java.util.function.Supplier;

/**
 * Central entry point for values addons need a stable handle to, without importing the base
 * mod's internal registration class directly. The base mod stays the single source of truth
 * for its own items — addons consume them through here so internal refactors don't break
 * downstream code.
 * <p>
 * Stable API surface — additive changes only once an addon depends on a version.
 */
public final class QuantumVoidApi {
    private QuantumVoidApi() {
    }

    /**
     * The Fractured Core boss's guaranteed drop (see docs/DESIGN.md — "Fractured Core
     * Precursor Item"). The Paradoxium tier and Singularity Core upgrade should consume this
     * reference rather than hardcoding an item ID.
     */
    public static Supplier<Item> singularitySeed() {
        return QuantumVoid.SINGULARITY_SEED;
    }
}
