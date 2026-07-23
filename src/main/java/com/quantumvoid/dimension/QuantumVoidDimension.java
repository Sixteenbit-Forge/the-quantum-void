package com.quantumvoid.dimension;

import com.quantumvoid.QuantumVoid;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public final class QuantumVoidDimension {
    // Matches data/quantumvoid/dimension/quantum_void.json
    public static final ResourceKey<Level> LEVEL_KEY = ResourceKey.create(Registries.DIMENSION,
            Identifier.fromNamespaceAndPath(QuantumVoid.MODID, "quantum_void"));

    // Phase 0: fixed arrival point until Quantum Pearl linking is implemented.
    public static final double ARRIVAL_X = 0.5;
    public static final double ARRIVAL_Y = 100.0;
    public static final double ARRIVAL_Z = 0.5;

    private QuantumVoidDimension() {
    }
}
