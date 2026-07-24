package com.quantumvoid.block;

import com.quantumvoid.QuantumVoid;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/** Remembers which Quantum Pearl link (if any) completed this portal ring — see {@link com.quantumvoid.portal.QuantumPortalLinkRegistry}. */
public class QuantumPortalBlockEntity extends BlockEntity {
    private long linkId;

    public QuantumPortalBlockEntity(BlockPos pos, BlockState state) {
        super(QuantumVoid.QUANTUM_PORTAL_BLOCK_ENTITY.get(), pos, state);
    }

    public long linkId() {
        return this.linkId;
    }

    public void setLinkId(long linkId) {
        this.linkId = linkId;
        this.setChanged();
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.linkId = input.getLongOr("link_id", 0L);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putLong("link_id", this.linkId);
    }
}
