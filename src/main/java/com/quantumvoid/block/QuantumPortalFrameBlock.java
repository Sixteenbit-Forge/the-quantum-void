package com.quantumvoid.block;

import com.quantumvoid.QuantumVoid;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Holds one Quantum Pearl. Modeled on End Portal Frame's activation pattern, but
 * arranged in a 2x2-opening / 8-frame ring instead of vanilla's 3x3/12-frame square.
 * See docs/DESIGN.md — "Portal — Access & Return Trip".
 */
public class QuantumPortalFrameBlock extends Block {
    public static final BooleanProperty FILLED = BooleanProperty.create("filled");

    // The 4 possible offsets (dx, dz) of this frame block relative to the interior's
    // origin (its lowest-x, lowest-z corner), one per ring side, mirrored for the two
    // positions along that side. Axis-aligned only — no rotated frames.
    private static final int[][] RING_OFFSETS = {
            // North side: interior origin is (this.x, this.z + 1)
            {0, 1}, {-1, 1},
            // South side: interior origin is (this.x, this.z - 2)
            {0, -2}, {-1, -2},
            // West side: interior origin is (this.x + 1, this.z)
            {1, 0}, {1, -1},
            // East side: interior origin is (this.x - 2, this.z)
            {-2, 0}, {-2, -1},
    };

    public QuantumPortalFrameBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FILLED, false));
    }

    @Override
    protected InteractionResult useItemOn(ItemStack heldItem, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hit) {
        if (state.getValue(FILLED) || !heldItem.is(QuantumVoid.QUANTUM_PEARL.get())) {
            return super.useItemOn(heldItem, state, level, pos, player, hand, hit);
        }

        if (!player.getAbilities().instabuild) {
            heldItem.shrink(1);
        }
        level.setBlock(pos, state.setValue(FILLED, true), 3);
        level.playSound(null, pos, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);

        if (!level.isClientSide()) {
            tryFormPortal(level, pos);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FILLED);
    }

    private void tryFormPortal(Level level, BlockPos filledFramePos) {
        for (int[] offset : RING_OFFSETS) {
            BlockPos interiorOrigin = filledFramePos.offset(offset[0], 0, offset[1]);
            if (ringIsComplete(level, interiorOrigin) && interiorIsClear(level, interiorOrigin)) {
                fillInterior(level, interiorOrigin);
                return;
            }
        }
    }

    private boolean ringIsComplete(Level level, BlockPos interiorOrigin) {
        for (BlockPos ringPos : ringPositions(interiorOrigin)) {
            BlockState state = level.getBlockState(ringPos);
            if (!state.is(this) || !state.getValue(FILLED)) {
                return false;
            }
        }
        return true;
    }

    private boolean interiorIsClear(Level level, BlockPos interiorOrigin) {
        for (BlockPos interiorPos : interiorPositions(interiorOrigin)) {
            if (!level.getBlockState(interiorPos).isAir()) {
                return false;
            }
        }
        return true;
    }

    private void fillInterior(Level level, BlockPos interiorOrigin) {
        BlockState portal = QuantumVoid.QUANTUM_PORTAL.get().defaultBlockState();
        for (BlockPos interiorPos : interiorPositions(interiorOrigin)) {
            level.setBlock(interiorPos, portal, 3);
        }
        level.playSound(null, interiorOrigin, SoundEvents.END_PORTAL_SPAWN, SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    private static Iterable<BlockPos> interiorPositions(BlockPos origin) {
        return java.util.List.of(origin, origin.offset(1, 0, 0), origin.offset(0, 0, 1), origin.offset(1, 0, 1));
    }

    private static Iterable<BlockPos> ringPositions(BlockPos origin) {
        return java.util.List.of(
                origin.offset(0, 0, -1), origin.offset(1, 0, -1),
                origin.offset(0, 0, 2), origin.offset(1, 0, 2),
                origin.offset(-1, 0, 0), origin.offset(-1, 0, 1),
                origin.offset(2, 0, 0), origin.offset(2, 0, 1));
    }
}
