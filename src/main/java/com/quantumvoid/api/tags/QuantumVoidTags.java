package com.quantumvoid.api.tags;

import com.quantumvoid.QuantumVoid;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

/**
 * Tier-gating block tags, mirroring vanilla's own {@code needs_iron_tool}/{@code needs_diamond_tool}
 * convention: tag an ore block with the tier it requires, and any tool honoring the same tag
 * (via a matching {@code minecraft:mineable/*}/tier tag setup) can harvest it.
 * <p>
 * Deliberately data-driven rather than {@code instanceof} checks against specific item
 * classes — an addon can slot a brand-new tier into the same chain purely through datapack
 * tag files, no code dependency on this mod required.
 * <p>
 * Stable API surface — additive changes only once an addon depends on a version.
 */
public final class QuantumVoidTags {
    private QuantumVoidTags() {
    }

    public static final TagKey<Block> NEEDS_CHARGED_CERTUS_TIER = block("needs_charged_certus_tier");
    public static final TagKey<Block> NEEDS_FLUXSTONE_TIER = block("needs_fluxstone_tier");
    public static final TagKey<Block> NEEDS_QUANTUMITE_TIER = block("needs_quantumite_tier");
    public static final TagKey<Block> NEEDS_PARADOXIUM_TIER = block("needs_paradoxium_tier");

    private static TagKey<Block> block(String path) {
        return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(QuantumVoid.MODID, path));
    }
}
