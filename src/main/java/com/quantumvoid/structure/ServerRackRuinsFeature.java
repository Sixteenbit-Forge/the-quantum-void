package com.quantumvoid.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.storage.loot.LootTable;

/** Light loot, no guards — see docs/DESIGN.md, "Server rack ruins". */
public class ServerRackRuinsFeature extends AbstractRuinFeature {
    private static final ResourceKey<LootTable> LOOT_TABLE =
            ResourceKey.create(net.minecraft.core.registries.Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("quantumvoid", "structures/server_rack_ruins"));

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();

        hollowBox(level, origin.offset(-2, 0, -2), origin.offset(2, 3, 2), Blocks.CHISELED_TUFF.defaultBlockState());
        fillBox(level, origin.offset(-2, 0, -2), origin.offset(2, 0, 2), net.minecraft.world.level.block.Blocks.END_STONE_BRICKS.defaultBlockState());
        placeLootChest(level, context.random(), origin.offset(0, 1, -1), LOOT_TABLE);
        return true;
    }
}
