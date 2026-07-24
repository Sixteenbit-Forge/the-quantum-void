package com.quantumvoid.portal;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Maps a Quantum Pearl link ID to the most recently completed portal ring carrying it, so the
 * other half of a linked pair can find its destination. In-memory only — does not persist across
 * a server restart, a scoped-down simplification versus a real {@code SavedData}-backed registry
 * (see docs/DESIGN.md "Portal — Quantum Pearl linking").
 */
public final class QuantumPortalLinkRegistry {
    private QuantumPortalLinkRegistry() {
    }

    private static final Map<Long, GlobalPos> LINKS = new HashMap<>();

    public static void register(long linkId, ResourceKey<Level> dimension, BlockPos pos) {
        if (linkId != 0) {
            LINKS.put(linkId, GlobalPos.of(dimension, pos));
        }
    }

    /** @return the other end of the link, if one exists and isn't this exact portal itself. */
    public static Optional<GlobalPos> findOtherEnd(long linkId, ResourceKey<Level> selfDimension, BlockPos selfPos) {
        if (linkId == 0) {
            return Optional.empty();
        }
        GlobalPos found = LINKS.get(linkId);
        if (found == null || (found.dimension() == selfDimension && found.pos().equals(selfPos))) {
            return Optional.empty();
        }
        return Optional.of(found);
    }
}
