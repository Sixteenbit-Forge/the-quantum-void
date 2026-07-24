package com.quantumvoid.api.ore;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

/**
 * Lets addons register ore blocks for the Fractured Core dimension's world-gen to scatter,
 * without editing the base mod's biome/feature files at all.
 * <p>
 * The base mod wires one feature slot per biome (regular and motherboard) that reads this
 * registry's current entries at placement time — so registering here is enough to have an
 * ore show up in-world; call {@link #register(OreEntry)} once at mod construction time.
 * <p>
 * Stable API surface — additive changes only once an addon depends on a version.
 */
public final class FractureCoreOreRegistry {
    private static final List<OreEntry> ENTRIES = new CopyOnWriteArrayList<>();

    private FractureCoreOreRegistry() {
    }

    public static void register(OreEntry entry) {
        ENTRIES.add(Objects.requireNonNull(entry, "entry"));
    }

    public static List<OreEntry> entries() {
        return List.copyOf(ENTRIES);
    }

    /**
     * @param ore              the ore block to place.
     * @param countPerChunkAttempt how many placement attempts per chunk this ore gets, same scale as
     *                         vanilla's {@code minecraft:count} placement (tune relative to rarity desired).
     * @param minY             inclusive minimum world height this ore can appear at.
     * @param maxY             inclusive maximum world height this ore can appear at.
     * @param motherboardOnly  if true, this ore only scatters on rare "motherboard" islands, not regular ones.
     */
    public record OreEntry(Supplier<Block> ore, int countPerChunkAttempt, int minY, int maxY, boolean motherboardOnly) {
        public OreEntry {
            Objects.requireNonNull(ore, "ore");
            if (countPerChunkAttempt <= 0) {
                throw new IllegalArgumentException("countPerChunkAttempt must be positive");
            }
            if (minY > maxY) {
                throw new IllegalArgumentException("minY must be <= maxY");
            }
        }

        public BlockState defaultState() {
            return ore.get().defaultBlockState();
        }
    }
}
