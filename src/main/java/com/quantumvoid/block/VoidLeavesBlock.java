package com.quantumvoid.block;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;

public class VoidLeavesBlock extends LeavesBlock {
    public static final MapCodec<VoidLeavesBlock> CODEC = Block.simpleCodec(VoidLeavesBlock::new);

    public VoidLeavesBlock(Properties properties) {
        super(0.05f, properties);
    }

    @Override
    public MapCodec<? extends VoidLeavesBlock> codec() {
        return CODEC;
    }

    @Override
    protected void spawnFallingLeavesParticle(Level level, BlockPos pos, RandomSource random) {
        // No ambient falling-leaves particle for Phase 1 — purely decorative block for now.
    }
}
