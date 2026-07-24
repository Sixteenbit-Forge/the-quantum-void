package com.quantumvoid.worldgen;

import com.quantumvoid.api.ore.FractureCoreOreRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;

import java.util.Optional;

/**
 * Reads {@link FractureCoreOreRegistry} at placement time and scatters each registered ore
 * as a real vanilla ore vein (delegating to {@link Feature#ORE} per entry) — the world-gen
 * side of the addon ore API. One instance of this feature is wired into each biome's
 * UNDERGROUND_ORES slot; {@link RegistryOreConfiguration#motherboardOnly()} controls whether
 * motherboard-exclusive entries are included for this particular placement.
 */
public class RegistryDrivenOreFeature extends Feature<RegistryOreConfiguration> {
    private static final int VEIN_SIZE = 4;

    public RegistryDrivenOreFeature() {
        super(RegistryOreConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<RegistryOreConfiguration> context) {
        WorldGenLevel level = context.level();
        ChunkGenerator generator = context.chunkGenerator();
        RandomSource random = context.random();
        BlockPos chunkOrigin = context.origin();
        boolean includeMotherboardOnly = context.config().motherboardOnly();

        Registry<net.minecraft.world.level.block.Block> blockRegistry = BuiltInRegistries.BLOCK;
        net.minecraft.world.level.block.Block skyStone = blockRegistry.getValue(Identifier.fromNamespaceAndPath("ae2", "sky_stone_block"));

        boolean placedAny = false;
        for (FractureCoreOreRegistry.OreEntry entry : FractureCoreOreRegistry.entries()) {
            if (entry.motherboardOnly() && !includeMotherboardOnly) {
                continue;
            }
            for (int i = 0; i < entry.countPerChunkAttempt(); i++) {
                int x = chunkOrigin.getX() + random.nextInt(16);
                int z = chunkOrigin.getZ() + random.nextInt(16);
                int range = entry.maxY() - entry.minY() + 1;
                int y = entry.minY() + random.nextInt(range);
                BlockPos pos = new BlockPos(x, y, z);

                OreConfiguration oreConfig = new OreConfiguration(new BlockMatchTest(skyStone), entry.defaultState(), VEIN_SIZE);
                FeaturePlaceContext<OreConfiguration> oreContext =
                        new FeaturePlaceContext<>(Optional.empty(), level, generator, random, pos, oreConfig);
                if (ORE.place(oreContext)) {
                    placedAny = true;
                }
            }
        }
        return placedAny;
    }
}
