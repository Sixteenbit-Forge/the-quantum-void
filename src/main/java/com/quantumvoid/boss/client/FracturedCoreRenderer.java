package com.quantumvoid.boss.client;

import com.quantumvoid.boss.FracturedCoreEntity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;

/** Reuses vanilla's generic humanoid rig (player layer) with our own "corrupted AE2 operator" skin. */
public class FracturedCoreRenderer extends HumanoidMobRenderer<FracturedCoreEntity, HumanoidRenderState, HumanoidModel<HumanoidRenderState>> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("quantumvoid", "textures/entity/fractured_core.png");

    public FracturedCoreRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5f);
    }

    @Override
    public HumanoidRenderState createRenderState() {
        return new HumanoidRenderState();
    }

    @Override
    public Identifier getTextureLocation(HumanoidRenderState state) {
        return TEXTURE;
    }
}
