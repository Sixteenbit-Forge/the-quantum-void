package com.quantumvoid.recipe;

import com.quantumvoid.QuantumVoid;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class QuantumRecipes {
    private QuantumRecipes() {
    }

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, QuantumVoid.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<QuantumPearlPairingRecipe>> QUANTUM_PEARL_PAIRING =
            RECIPE_SERIALIZERS.register("quantum_pearl_pairing", () -> QuantumPearlPairingRecipe.SERIALIZER);
}
