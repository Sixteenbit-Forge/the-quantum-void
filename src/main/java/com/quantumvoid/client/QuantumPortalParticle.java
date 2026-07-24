package com.quantumvoid.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.PortalParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

/**
 * Reuses vanilla's own soft-glow particle sprite (see
 * assets/quantumvoid/particles/quantum_portal.json) but tints it cyan instead of the vanilla
 * Nether Portal's purple, matching the block's own MapColor.COLOR_CYAN and the recolored
 * quantum_portal.png texture. See docs/DESIGN.md "Portal recolor".
 */
public class QuantumPortalParticle extends PortalParticle {
    private QuantumPortalParticle(ClientLevel level, double x, double y, double z,
            double xSpeed, double ySpeed, double zSpeed, TextureAtlasSprite sprite) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed, sprite);
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource random) {
            QuantumPortalParticle particle = new QuantumPortalParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet.get(random));
            particle.setColor(0.15F, 0.85F, 0.9F);
            return particle;
        }
    }
}
