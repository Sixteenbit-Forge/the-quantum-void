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

    // Nominal arrival point for portal travel without a linked pearl pair.
    // QuantumPortalBlock randomizes the actual landing column around this point
    // rather than using it directly — in the end_islands density function we
    // reuse for terrain shaping, the origin (0,0) is specifically the dominant
    // "main island" and is the flattest, most solid point on the whole map by
    // design, so always landing exactly here would always land on the flattest
    // possible terrain.
    public static final double ARRIVAL_X = 0.5;
    public static final double ARRIVAL_Y = 100.0;
    public static final double ARRIVAL_Z = 0.5;

    public static final double OVERWORLD_ARRIVAL_X = 0.5;
    public static final double OVERWORLD_ARRIVAL_Y = 100.0;
    public static final double OVERWORLD_ARRIVAL_Z = 0.5;

    private QuantumVoidDimension() {
    }
}
