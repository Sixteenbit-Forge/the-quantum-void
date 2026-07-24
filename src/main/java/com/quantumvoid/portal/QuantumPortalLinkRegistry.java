package com.quantumvoid.portal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.Optional;

/**
 * Maps a Quantum Pearl link ID to the most recently completed portal ring carrying it, so the
 * other half of a linked pair can find its destination. Backed by {@link QuantumPortalLinkData},
 * a real {@code SavedData} kept on the overworld's data storage (regardless of which dimension
 * a given link was actually made in) — persists across a server restart.
 */
public final class QuantumPortalLinkRegistry {
    private QuantumPortalLinkRegistry() {
    }

    private static QuantumPortalLinkData data(ServerLevel level) {
        ServerLevel overworld = level.getServer().overworld();
        return overworld.getDataStorage().computeIfAbsent(QuantumPortalLinkData.TYPE);
    }

    public static void register(ServerLevel level, long linkId, ResourceKey<Level> dimension, BlockPos pos) {
        if (linkId != 0) {
            data(level).put(linkId, GlobalPos.of(dimension, pos));
        }
    }

    /** @return the other end of the link, if one exists and isn't this exact portal itself. */
    public static Optional<GlobalPos> findOtherEnd(ServerLevel level, long linkId, ResourceKey<Level> selfDimension, BlockPos selfPos) {
        if (linkId == 0) {
            return Optional.empty();
        }
        GlobalPos found = data(level).links().get(linkId);
        if (found == null || (found.dimension() == selfDimension && found.pos().equals(selfPos))) {
            return Optional.empty();
        }
        return Optional.of(found);
    }
}
