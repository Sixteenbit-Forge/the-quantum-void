package com.quantumvoid.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.storage.loot.LootTable;

/** Best minor-structure loot, rarer than the other three, guarded by more Fragments — see docs/DESIGN.md, "Processor silo". */
public class ProcessorSiloFeature extends AbstractRuinFeature {
    private static final ResourceKey<LootTable> LOOT_TABLE =
            ResourceKey.create(net.minecraft.core.registries.Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("quantumvoid", "structures/processor_silo"));

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();

        hollowBox(level, origin.offset(-3, 0, -3), origin.offset(3, 5, 3), Blocks.DEEPSLATE_TILES.defaultBlockState());
        fillBox(level, origin.offset(-1, 1, -1), origin.offset(1, 4, 1), Blocks.SEA_LANTERN.defaultBlockState());
        fillBox(level, origin.offset(0, 2, 0), origin.offset(0, 3, 0), Blocks.CAVE_AIR.defaultBlockState());
        placeLootChest(level, context.random(), origin.offset(2, 1, 2), LOOT_TABLE);
        placeLootChest(level, context.random(), origin.offset(-2, 1, -2), LOOT_TABLE);
        spawnGuard(level, origin.offset(2, 1, -2), false);
        spawnGuard(level, origin.offset(-2, 1, 2), true);
        spawnGuard(level, origin.offset(0, 1, 3), true);
        return true;
    }
}
