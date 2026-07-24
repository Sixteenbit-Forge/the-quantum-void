package com.quantumvoid.block;

import com.quantumvoid.QuantumVoid;
import com.quantumvoid.entity.FragmentMeleeEntity;
import com.quantumvoid.entity.FragmentRangedEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A dormant Fragment core that visibly cracks through growth stages over time (like a turtle
 * egg) before hatching into a real Fragment mob. Requires a sturdy floor beneath it, same
 * support check as {@link com.quantumvoid.block.QuantumPortalFrameBlock}'s neighbors. Not
 * placed by world-gen yet — hand/creative only, see docs/DESIGN.md.
 */
public class FragmentCocoonBlock extends Block {
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 4);
    private static final int MAX_STAGE = 4;
    private static final VoxelShape SHAPE = box(4, 0, 4, 12, 10, 12);

    public FragmentCocoonBlock(Properties properties) {
        super(properties.randomTicks());
        registerDefaultState(stateDefinition.any().setValue(STAGE, 0));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(STAGE);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!this.canSurvive(state, level, pos)) {
            level.destroyBlock(pos, true);
            return;
        }
        if (random.nextInt(20) != 0) {
            return;
        }

        int stage = state.getValue(STAGE);
        if (stage < MAX_STAGE) {
            level.setBlock(pos, state.setValue(STAGE, stage + 1), 2);
            return;
        }

        level.removeBlock(pos, false);
        Mob fragment = random.nextBoolean()
                ? new FragmentRangedEntity(QuantumVoid.FRAGMENT_RANGED.get(), level)
                : new FragmentMeleeEntity(QuantumVoid.FRAGMENT_MELEE.get(), level);
        fragment.snapTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
        fragment.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), EntitySpawnReason.MOB_SUMMONED, null);
        level.addFreshEntity(fragment);
    }
}
