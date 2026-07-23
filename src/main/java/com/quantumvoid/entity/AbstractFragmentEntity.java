package com.quantumvoid.entity;

import com.quantumvoid.QuantumVoid;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;

/**
 * Shared base for the Fragment mob family (see docs/DESIGN.md — "Mobs — Fragments"):
 * small floating cable-cluster enemies that debuff nearby players while alive.
 */
public abstract class AbstractFragmentEntity extends Monster {
    private static final double DRAIN_RADIUS = 16.0;

    protected AbstractFragmentEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.setNoGravity(true);
        this.setPathfindingMalus(PathType.FIRE, 0.0f);
        this.setPathfindingMalus(PathType.WATER, 0.0f);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        return navigation;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 10.0)
                .add(Attributes.FLYING_SPEED, 0.6)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    @Override
    protected void customServerAiStep(net.minecraft.server.level.ServerLevel level) {
        super.customServerAiStep(level);
        applyChannelDrainAura();
    }

    private void applyChannelDrainAura() {
        AABB aura = this.getBoundingBox().inflate(DRAIN_RADIUS);
        for (Player player : level().getEntitiesOfClass(Player.class, aura)) {
            player.addEffect(new MobEffectInstance(QuantumVoid.CHANNEL_DRAIN, 100, 0, false, true, true));
        }
    }
}
