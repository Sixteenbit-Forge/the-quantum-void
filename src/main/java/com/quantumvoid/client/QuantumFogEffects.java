package com.quantumvoid.client;

import com.quantumvoid.boss.FracturedCoreEntity;
import com.quantumvoid.dimension.QuantumVoidDimension;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ViewportEvent;

/**
 * Reactive fog for the Quantum Void: the closer the camera is to a live Fractured Core boss,
 * the more the ambient fog bleeds toward its warning-red glow, instead of the dimension's
 * otherwise-static cyan fog_color. Client-only — see docs/DESIGN.md "Dimension fog effects".
 */
public final class QuantumFogEffects {
    private QuantumFogEffects() {
    }

    private static final double AWARENESS_RADIUS = 64.0;
    private static final float WARNING_RED = 0.55F;
    private static final float WARNING_GREEN = 0.05F;
    private static final float WARNING_BLUE = 0.08F;

    public static void onComputeFogColor(ViewportEvent.ComputeFogColor event) {
        Entity cameraEntity = event.getCamera().entity();
        if (!(cameraEntity.level() instanceof ClientLevel level) || level.dimension() != QuantumVoidDimension.LEVEL_KEY) {
            return;
        }

        Vec3 pos = event.getCamera().position();
        AABB searchBox = new AABB(pos, pos).inflate(AWARENESS_RADIUS);
        double nearestDistance = level.getEntitiesOfClass(FracturedCoreEntity.class, searchBox).stream()
                .mapToDouble(core -> core.position().distanceTo(pos))
                .min()
                .orElse(Double.MAX_VALUE);

        if (nearestDistance >= AWARENESS_RADIUS) {
            return;
        }

        float intensity = (float) (1.0 - nearestDistance / AWARENESS_RADIUS);
        event.setRed(lerp(event.getRed(), WARNING_RED, intensity));
        event.setGreen(lerp(event.getGreen(), WARNING_GREEN, intensity));
        event.setBlue(lerp(event.getBlue(), WARNING_BLUE, intensity));
    }

    private static float lerp(float from, float to, float t) {
        return from + (to - from) * t;
    }
}
