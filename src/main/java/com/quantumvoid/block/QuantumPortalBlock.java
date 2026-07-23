package com.quantumvoid.block;

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

        BlockPos targetColumn = inQuantumVoid
                ? BlockPos.containing(QuantumVoidDimension.OVERWORLD_ARRIVAL_X, QuantumVoidDimension.OVERWORLD_ARRIVAL_Y, QuantumVoidDimension.OVERWORLD_ARRIVAL_Z)
                : BlockPos.containing(QuantumVoidDimension.ARRIVAL_X, QuantumVoidDimension.ARRIVAL_Y, QuantumVoidDimension.ARRIVAL_Z);
        Vec3 arrivalPos = findSafeLanding(destination, targetColumn);

        entity.setPortalCooldown();
        entity.teleport(new TeleportTransition(destination, arrivalPos, Vec3.ZERO,
                entity.getYRot(), entity.getXRot(), TeleportTransition.DO_NOTHING));
    }

    /**
     * The Quantum Void is sparse floating islands, not a guaranteed-solid column like the
     * Overworld — a fixed arrival point can land inside terrain or in open air over nothing.
     * Search outward in the target column, then in a widening ring of nearby columns, for the
     * first real ground surface (chunk-forced, mirrors the pattern used by a reference project's
     * a reference project for its own dimension-entry landing search) and land on top of it.
     */
    private static Vec3 findSafeLanding(ServerLevel level, BlockPos target) {
        for (int radius = 0; radius <= 32; radius += 8) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != radius) {
                        continue;
                    }
                    int x = target.getX() + dx;
                    int z = target.getZ() + dz;
                    level.getChunk(x >> 4, z >> 4);
                    int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
                    if (surfaceY > level.getMinY()) {
                        return new Vec3(x + 0.5, surfaceY, z + 0.5);
                    }
                }
            }
        }
        return Vec3.atCenterOf(target);
    }
}
