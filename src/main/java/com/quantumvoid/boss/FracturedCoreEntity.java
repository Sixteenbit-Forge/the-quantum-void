package com.quantumvoid.boss;

import com.quantumvoid.QuantumVoid;
import com.quantumvoid.entity.FragmentMeleeEntity;
import com.quantumvoid.entity.FragmentRangedEntity;

import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.BossEvent;

import java.util.EnumSet;

/**
 * The capstone boss guarding The Fractured Core (see docs/DESIGN.md — "Boss — Fractured Core").
 * Humanoid scale, Lich-style multi-phase kit: blink around the arena, summon Fragment adds,
 * and periodically sever every nearby player's channel access for a short window.
 */
public class FracturedCoreEntity extends Monster {
    private static final double ARENA_RADIUS = 24.0;

    private final ServerBossEvent bossEvent = new ServerBossEvent(Mth.createInsecureUUID(this.random), this.getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS);

    public FracturedCoreEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 50;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 150.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.ATTACK_DAMAGE, 8.0)
                .add(Attributes.FOLLOW_RANGE, ARENA_RADIUS)
                .add(Attributes.ARMOR, 6.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new BlinkGoal());
        this.goalSelector.addGoal(1, new SummonFragmentsGoal());
        this.goalSelector.addGoal(1, new ChannelSeverGoal());
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, true));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    private AABB arena() {
        return this.getBoundingBox().inflate(ARENA_RADIUS);
    }

    /** Teleport/blink mobility phase. */
    private class BlinkGoal extends Goal {
        private int cooldown = 100;

        BlinkGoal() {
            this.setFlags(EnumSet.noneOf(Goal.Flag.class));
        }

        @Override
        public boolean canUse() {
            return getTarget() != null && --cooldown <= 0;
        }

        @Override
        public void start() {
            Player target = (Player) getTarget();
            if (target != null) {
                double angle = random.nextDouble() * Math.PI * 2;
                double dist = 4.0 + random.nextDouble() * 4.0;
                double x = target.getX() + Math.cos(angle) * dist;
                double z = target.getZ() + Math.sin(angle) * dist;
                teleportTo(x, target.getY(), z);
            }
            cooldown = 100 + random.nextInt(60);
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }
    }

    /** Summons Fragment adds — see docs/DESIGN.md, "not a Fragment-swarm/escort-wave fight" on its own, but adds pressure mid-fight. */
    private class SummonFragmentsGoal extends Goal {
        private int cooldown = 200;

        SummonFragmentsGoal() {
            this.setFlags(EnumSet.noneOf(Goal.Flag.class));
        }

        @Override
        public boolean canUse() {
            return getTarget() != null && --cooldown <= 0;
        }

        @Override
        public void start() {
            if (level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                for (int i = 0; i < 3; i++) {
                    net.minecraft.world.entity.Mob fragment = i == 0
                            ? new FragmentRangedEntity(QuantumVoid.FRAGMENT_RANGED.get(), level())
                            : new FragmentMeleeEntity(QuantumVoid.FRAGMENT_MELEE.get(), level());
                    double angle = i * (Math.PI * 2 / 3);
                    fragment.snapTo(getX() + Math.cos(angle) * 3.0, getY() + 1.0, getZ() + Math.sin(angle) * 3.0, 0, 0);
                    fragment.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(blockPosition()), net.minecraft.world.entity.EntitySpawnReason.MOB_SUMMONED, null);
                    serverLevel.addFreshEntity(fragment);
                }
            }
            cooldown = 400 + random.nextInt(200);
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }
    }

    /** Channel-severing attack: burst-applies the channel drain debuff to every player in the arena. */
    private class ChannelSeverGoal extends Goal {
        private int cooldown = 260;

        ChannelSeverGoal() {
            this.setFlags(EnumSet.noneOf(Goal.Flag.class));
        }

        @Override
        public boolean canUse() {
            return getTarget() != null && --cooldown <= 0;
        }

        @Override
        public void start() {
            for (Player player : level().getEntitiesOfClass(Player.class, arena())) {
                player.addEffect(new MobEffectInstance(QuantumVoid.CHANNEL_DRAIN, 200, 1, false, true, true));
            }
            level().playSound(null, blockPosition(), net.minecraft.sounds.SoundEvents.WARDEN_SONIC_BOOM, net.minecraft.sounds.SoundSource.HOSTILE, 1.0f, 0.6f);
            cooldown = 500 + random.nextInt(200);
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }
    }
}
