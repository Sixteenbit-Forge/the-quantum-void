package com.quantumvoid.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.storage.loot.LootTable;

/** Light loot, low combat — see docs/DESIGN.md, "Crashed cable-spool husk". */
public class CableSpoolHuskFeature extends AbstractRuinFeature {
    private static final ResourceKey<LootTable> LOOT_TABLE =
            ResourceKey.create(net.minecraft.core.registries.Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("quantumvoid", "structures/cable_spool_husk"));

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();

        hollowBox(level, origin.offset(-2, 0, -3), origin.offset(2, 2, 3), Blocks.POLISHED_BASALT.defaultBlockState());
        placeLootChest(level, context.random(), origin.offset(0, 1, 2), LOOT_TABLE);
        if (context.random().nextFloat() < 0.5f) {
            spawnGuard(level, origin.offset(0, 1, -2), false);
        }
        return true;
    }
}
