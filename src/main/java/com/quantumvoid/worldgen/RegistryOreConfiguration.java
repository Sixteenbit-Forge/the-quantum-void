package com.quantumvoid.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

/** @param motherboardOnly whether this placement includes {@code motherboardOnly} registry entries. */
public record RegistryOreConfiguration(boolean motherboardOnly) implements FeatureConfiguration {
    public static final Codec<RegistryOreConfiguration> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codec.BOOL.fieldOf("motherboard_only").forGetter(RegistryOreConfiguration::motherboardOnly))
                    .apply(instance, RegistryOreConfiguration::new));
}
