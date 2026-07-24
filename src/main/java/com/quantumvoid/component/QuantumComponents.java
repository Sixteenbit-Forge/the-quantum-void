package com.quantumvoid.component;

import com.quantumvoid.QuantumVoid;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * A Quantum Pearl's link ID, a plain {@code long} (0 = unlinked/generic filler pearl). Two
 * pearls crafted together share the same freshly-generated nonzero ID — see
 * {@link com.quantumvoid.recipe.QuantumPearlPairingRecipe}.
 */
public final class QuantumComponents {
    private QuantumComponents() {
    }

    public static final DeferredRegister.DataComponents DATA_COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, QuantumVoid.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> PEARL_LINK =
            DATA_COMPONENTS.registerComponentType("pearl_link",
                    builder -> builder.persistent(com.mojang.serialization.Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG));
}
