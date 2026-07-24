package com.quantumvoid.structure;

import com.quantumvoid.QuantumVoid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * The capstone structure — "The Fractured Core" (see docs/DESIGN.md — "Structures — Capstone
 * structure"). Motherboard-island only. Houses the boss and a guaranteed flawless budding
 * certus quartz cluster.
 */
public class FracturedCoreFeature extends AbstractRuinFeature {
    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();

        hollowBox(level, origin.offset(-5, 0, -5), origin.offset(5, 6, 5), Blocks.CHISELED_DEEPSLATE.defaultBlockState());
        fillBox(level, origin.offset(-1, 1, -1), origin.offset(1, 1, 1), Blocks.CHISELED_TUFF.defaultBlockState());

        BlockState flawless = BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath("ae2", "flawless_budding_quartz")).defaultBlockState();
        level.setBlock(origin.offset(0, 2, 0), flawless, 3);

        if (level.getLevel() instanceof ServerLevel serverLevel) {
            var boss = new com.quantumvoid.boss.FracturedCoreEntity(QuantumVoid.FRACTURED_CORE.get(), serverLevel);
            boss.snapTo(origin.getX() + 0.5, origin.getY() + 1.0, origin.getZ() + 0.5, 0, 0);
            boss.finalizeSpawn(serverLevel, level.getCurrentDifficultyAt(origin), EntitySpawnReason.STRUCTURE, null);
            serverLevel.addFreshEntity(boss);
        }
        return true;
    }
}
