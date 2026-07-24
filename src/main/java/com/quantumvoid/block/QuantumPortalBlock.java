package com.quantumvoid.block;

import com.quantumvoid.api.event.QuantumPortalTravelEvent;
import com.quantumvoid.dimension.QuantumVoidDimension;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

/**
 * The interior "you're standing in the portal" block, filled in by
 * {@link QuantumPortalFrameBlock} once a ring is complete. Not directly obtainable.
 * <p>
 * Phase 0: teleports to a fixed destination per direction. Quantum Pearl-based
 * arbitrary linking between specific frame pairs is not implemented yet.
 */
public class QuantumPortalBlock extends Block {
    public QuantumPortalBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity,
            InsideBlockEffectApplier effectApplier, boolean isFirstTick) {
        if (!(level instanceof ServerLevel serverLevel) || entity.isOnPortalCooldown()) {
            return;
        }

        boolean inQuantumVoid = serverLevel.dimension() == QuantumVoidDimension.LEVEL_KEY;
        ServerLevel destination = inQuantumVoid
                ? serverLevel.getServer().getLevel(Level.OVERWORLD)
                : serverLevel.getServer().getLevel(QuantumVoidDimension.LEVEL_KEY);
        if (destination == null) {
            return;
        }

        BlockPos targetColumn;
        if (inQuantumVoid) {
            targetColumn = BlockPos.containing(QuantumVoidDimension.OVERWORLD_ARRIVAL_X, QuantumVoidDimension.OVERWORLD_ARRIVAL_Y, QuantumVoidDimension.OVERWORLD_ARRIVAL_Z);
        } else {
            // Randomize around the nominal point rather than using it directly, so
            // arrival doesn't always land on end_islands' flat, dominant origin island.
            var random = serverLevel.getRandom();
            int offsetX = random.nextInt(1601) - 800;
            int offsetZ = random.nextInt(1601) - 800;
            targetColumn = BlockPos.containing(QuantumVoidDimension.ARRIVAL_X + offsetX, QuantumVoidDimension.ARRIVAL_Y, QuantumVoidDimension.ARRIVAL_Z + offsetZ);
        }
        Vec3 arrivalPos = findSafeLanding(destination, targetColumn)
                // Random search came up empty (sparse area) — retry at world origin, which
                // end_islands guarantees to be solid (it's the dominant "main island").
                .or(() -> findSafeLanding(destination, BlockPos.containing(0.5, QuantumVoidDimension.ARRIVAL_Y, 0.5)))
                .orElseGet(() -> Vec3.atCenterOf(targetColumn));

        entity.setPortalCooldown();
        entity.teleport(new TeleportTransition(destination, arrivalPos, Vec3.ZERO,
                entity.getYRot(), entity.getXRot(), TeleportTransition.DO_NOTHING));
        QuantumPortalTravelEvent.post(entity, serverLevel, destination, !inQuantumVoid);
    }

    /**
     * The Quantum Void is sparse floating islands, not a guaranteed-solid column like the
     * Overworld — a fixed arrival point can land inside terrain or in open air over nothing.
     * Search outward in the target column, then in a widening ring of nearby columns, for the
     * first real ground surface (chunk-forced, mirrors the pattern used by TelepathicGrunt's
     * Bumblezone for its own dimension-entry landing search) and land on top of it.
     */
    private static final int EDGE_CLEARANCE = 5;

    private static Optional<Vec3> findSafeLanding(ServerLevel level, BlockPos target) {
        for (int radius = 0; radius <= 128; radius += 8) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != radius) {
                        continue;
                    }
                    int x = target.getX() + dx;
                    int z = target.getZ() + dz;
                    level.getChunk(x >> 4, z >> 4);
                    int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
                    if (surfaceY > level.getMinY() && isAwayFromEdge(level, x, z, surfaceY)) {
                        return Optional.of(new Vec3(x + 0.5, surfaceY, z + 0.5));
                    }
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Confirms ground extends at least {@link #EDGE_CLEARANCE} blocks in every cardinal
     * direction at roughly the same height, so players don't land right on a cliff edge.
     */
    private static boolean isAwayFromEdge(ServerLevel level, int x, int z, int surfaceY) {
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] dir : directions) {
            int nx = x + dir[0] * EDGE_CLEARANCE;
            int nz = z + dir[1] * EDGE_CLEARANCE;
            level.getChunk(nx >> 4, nz >> 4);
            int neighborY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, nx, nz);
            if (Math.abs(neighborY - surfaceY) > 3) {
                return false;
            }
        }
        return true;
    }
}
