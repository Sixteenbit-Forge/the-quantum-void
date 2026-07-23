package com.quantumvoid.block;

import appeng.api.networking.GridFlags;
import appeng.api.networking.GridHelper;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.api.networking.IManagedGridNode;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;

/**
 * See docs/DESIGN.md — "Void Sky Stone": an alternate cable block that hosts a grid node
 * and carries channels itself (AE2's DENSE_CAPACITY flag), via the same public
 * IInWorldGridNodeHost/GridHelper API AE2 uses internally for its own cables.
 * <p>
 * Implements IInWorldGridNodeHost directly against appeng.api.networking rather than
 * extending AE2's internal appeng.blockentity.grid.AENetworkedBlockEntity — that
 * convenience base isn't part of the published API jar this mod compiles against.
 */
public class VoidSkyStoneBlockEntity extends BlockEntity implements IInWorldGridNodeHost {
    private final IManagedGridNode mainNode = GridHelper.createManagedNode(this, (owner, node) -> {})
            .setInWorldNode(true)
            .setFlags(GridFlags.DENSE_CAPACITY)
            .setIdlePowerUsage(0.0)
            .setExposedOnSides(EnumSet.allOf(Direction.class));

    public VoidSkyStoneBlockEntity(BlockPos pos, BlockState state) {
        super(com.quantumvoid.QuantumVoid.VOID_SKY_STONE_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        this.mainNode.destroy();
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        this.mainNode.create(this.level, this.worldPosition);
    }

    @Override
    public appeng.api.networking.IGridNode getGridNode(Direction direction) {
        return this.mainNode.getNode();
    }
}
