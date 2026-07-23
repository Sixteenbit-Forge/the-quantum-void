package com.quantumvoid.entity;

import com.quantumvoid.QuantumVoid;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/** Fragment ranged variant's projectile — reuses the Quantum Pearl icon so it needs no new sprite/renderer. */
public class FragmentBoltEntity extends ThrowableItemProjectile {
    public FragmentBoltEntity(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public FragmentBoltEntity(Level level, LivingEntity owner) {
        super(QuantumVoid.FRAGMENT_BOLT.get(), owner, level, new ItemStack(QuantumVoid.QUANTUM_PEARL.get()));
    }

    @Override
    protected Item getDefaultItem() {
        return QuantumVoid.QUANTUM_PEARL.get();
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        Entity target = hitResult.getEntity();
        if (!level().isClientSide() && target instanceof LivingEntity living) {
            living.hurt(this.damageSources().thrown(this, this.getOwner()), 4.0f);
            living.addEffect(new MobEffectInstance(QuantumVoid.CHANNEL_DRAIN, 100, 0, false, true, true));
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!level().isClientSide()) {
            this.discard();
        }
    }
}
