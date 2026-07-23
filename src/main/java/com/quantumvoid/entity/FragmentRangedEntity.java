package com.quantumvoid.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class FragmentRangedEntity extends AbstractFragmentEntity implements RangedAttackMob {
    public FragmentRangedEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.0, 40, 12.0f));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomFlyingGoal(this, 1.0));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void performRangedAttack(LivingEntity target, float power) {
        FragmentBoltEntity bolt = new FragmentBoltEntity(this.level(), this);
        double dx = target.getX() - this.getX();
        double dy = target.getY(0.5) - bolt.getY();
        double dz = target.getZ() - this.getZ();
        bolt.shoot(dx, dy, dz, 1.6f, 6.0f);
        this.level().addFreshEntity(bolt);
    }
}
