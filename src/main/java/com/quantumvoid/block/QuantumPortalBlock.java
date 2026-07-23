package com.quantumvoid.block;

import com.quantumvoid.dimension.QuantumVoidDimension;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
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
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
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

        Vec3 arrivalPos = inQuantumVoid
                ? Vec3.atCenterOf(destination.getSharedSpawnPos())
                : new Vec3(QuantumVoidDimension.ARRIVAL_X, QuantumVoidDimension.ARRIVAL_Y, QuantumVoidDimension.ARRIVAL_Z);

        entity.setPortalCooldown();
        entity.teleport(new TeleportTransition(destination, arrivalPos, Vec3.ZERO,
                entity.getYRot(), entity.getXRot(), TeleportTransition.DO_NOTHING));
    }
}
