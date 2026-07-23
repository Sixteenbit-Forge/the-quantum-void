package com.quantumvoid.block;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;

public class VoidSandBlock extends FallingBlock {
    public static final MapCodec<VoidSandBlock> CODEC = Block.simpleCodec(VoidSandBlock::new);

    public VoidSandBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends VoidSandBlock> codec() {
        return CODEC;
    }

    @Override
    public int getDustColor(BlockState state, BlockGetter level, BlockPos pos) {
        return 0x6E4BD0;
    }
}
