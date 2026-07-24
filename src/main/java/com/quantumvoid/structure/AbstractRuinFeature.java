package com.quantumvoid.structure;

import com.quantumvoid.QuantumVoid;
import com.quantumvoid.entity.FragmentMeleeEntity;
import com.quantumvoid.entity.FragmentRangedEntity;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.server.level.ServerLevel;

/**
 * Shared block-placement helpers for the minor structure family (see docs/DESIGN.md —
 * "Structures — Minor structures"). Placed as regular world-gen Features rather than the
 * full vanilla Structure/StructurePiece pipeline — same "find a small ruin with loot and
 * guards" outcome, far less surface area to get wrong without in-game testing.
 */
public abstract class AbstractRuinFeature extends Feature<NoneFeatureConfiguration> {
    protected AbstractRuinFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    protected static void fillBox(WorldGenLevel level, BlockPos from, BlockPos to, BlockState state) {
        int minX = Math.min(from.getX(), to.getX());
        int maxX = Math.max(from.getX(), to.getX());
        int minY = Math.min(from.getY(), to.getY());
        int maxY = Math.max(from.getY(), to.getY());
        int minZ = Math.min(from.getZ(), to.getZ());
        int maxZ = Math.max(from.getZ(), to.getZ());
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    level.setBlock(new BlockPos(x, y, z), state, 3);
                }
            }
        }
    }

    protected static void hollowBox(WorldGenLevel level, BlockPos from, BlockPos to, BlockState wallState) {
        fillBox(level, from, to, wallState);
        BlockPos innerFrom = from.offset(1, 1, 1);
        BlockPos innerTo = to.offset(-1, -1, -1);
        if (innerFrom.getX() <= innerTo.getX() && innerFrom.getY() <= innerTo.getY() && innerFrom.getZ() <= innerTo.getZ()) {
            fillBox(level, innerFrom, innerTo, Blocks.CAVE_AIR.defaultBlockState());
        }
    }

    protected static void placeLootChest(WorldGenLevel level, RandomSource random, BlockPos pos, ResourceKey<LootTable> lootTable) {
        level.setBlock(pos, Blocks.CHEST.defaultBlockState(), 3);
        RandomizableContainer.setBlockEntityLootTable(level, random, pos, lootTable);
    }

    protected static void spawnGuard(WorldGenLevel level, BlockPos pos, boolean ranged) {
        ServerLevel serverLevel = level.getLevel();
        Mob fragment = ranged
                ? new FragmentRangedEntity(QuantumVoid.FRAGMENT_RANGED.get(), serverLevel)
                : new FragmentMeleeEntity(QuantumVoid.FRAGMENT_MELEE.get(), serverLevel);
        fragment.snapTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
        fragment.finalizeSpawn(serverLevel, level.getCurrentDifficultyAt(pos), EntitySpawnReason.STRUCTURE, null);
        serverLevel.addFreshEntity(fragment);
    }
}
