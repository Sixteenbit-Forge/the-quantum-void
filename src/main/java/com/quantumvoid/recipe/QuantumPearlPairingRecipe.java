package com.quantumvoid.recipe;

import com.mojang.serialization.MapCodec;

import com.quantumvoid.QuantumVoid;
import com.quantumvoid.component.QuantumComponents;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 2x Ender Pearl + 1x AE2 Fluix Crystal -> 2x Quantum Pearl, both sharing one freshly-generated
 * link ID (see {@link QuantumComponents#PEARL_LINK}) — a custom recipe rather than a plain
 * shaped/shapeless one since the result needs a fresh random value baked in at craft time, not a
 * fixed one. A deliberately simpler substitute for the brief's fuller "network-connected crafting
 * device with an active Quantum Ring formation" vision — see docs/DESIGN.md.
 */
public class QuantumPearlPairingRecipe extends CustomRecipe {
    public static final RecipeSerializer<QuantumPearlPairingRecipe> SERIALIZER =
            new RecipeSerializer<>(MapCodec.unit(QuantumPearlPairingRecipe::new), StreamCodec.unit(new QuantumPearlPairingRecipe()));

    private static Item fluixCrystal() {
        return BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("ae2", "fluix_crystal"));
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        int pearls = 0;
        int fluix = 0;
        Item fluixCrystal = fluixCrystal();
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.is(Items.ENDER_PEARL)) {
                pearls++;
            } else if (stack.is(fluixCrystal)) {
                fluix++;
            } else {
                return false;
            }
        }
        return pearls == 2 && fluix == 1;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        ItemStack result = new ItemStack(QuantumVoid.QUANTUM_PEARL.get(), 2);
        long linkId = ThreadLocalRandom.current().nextLong();
        if (linkId == 0) {
            linkId = 1;
        }
        result.set(QuantumComponents.PEARL_LINK.get(), linkId);
        return result;
    }

    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return SERIALIZER;
    }
}
