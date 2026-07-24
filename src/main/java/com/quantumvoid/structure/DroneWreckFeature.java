package com.quantumvoid.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.storage.loot.LootTable;

/** Combat-lite, acts as a small Fragment spawn nest — see docs/DESIGN.md, "Downed drone wreck". */
public class DroneWreckFeature extends AbstractRuinFeature {
    private static final ResourceKey<LootTable> LOOT_TABLE =
            ResourceKey.create(net.minecraft.core.registries.Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("quantumvoid", "structures/downed_drone_wreck"));

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();

        hollowBox(level, origin.offset(-3, 0, -2), origin.offset(3, 2, 2), Blocks.BLACKSTONE.defaultBlockState());
        fillBox(level, origin.offset(-1, 0, -1), origin.offset(1, 0, 1), Blocks.CRACKED_DEEPSLATE_BRICKS.defaultBlockState());
        placeLootChest(level, context.random(), origin.offset(2, 1, 0), LOOT_TABLE);
        spawnGuard(level, origin.offset(-2, 1, 0), false);
        spawnGuard(level, origin.offset(0, 1, 1), true);
        return true;
    }
}
