package com.quantumvoid.effect;

import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * Proximity debuff applied by Fragments (see docs/DESIGN.md — "Mobs — Fragments").
 * Deliberately self-contained (a plain MobEffect, no direct IGridNode manipulation) —
 * AE2's IPathingService is read-only, so real wireless-terminal-range reduction isn't
 * wired up yet. Movement/attack speed penalty stands in as the "network access reduced"
 * feel until that hook exists.
 */
public class ChannelDrainEffect extends MobEffect {
    public ChannelDrainEffect() {
        super(MobEffectCategory.HARMFUL, 0x8a3fd1);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, Identifier.fromNamespaceAndPath("quantumvoid", "channel_drain_speed"), -0.15, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        this.addAttributeModifier(Attributes.ATTACK_SPEED, Identifier.fromNamespaceAndPath("quantumvoid", "channel_drain_attack_speed"), -0.15, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }
}
