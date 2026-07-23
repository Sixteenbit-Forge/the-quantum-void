package com.quantumvoid.entity.client;

import com.quantumvoid.entity.AbstractFragmentEntity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.endermite.EndermiteModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

/**
 * Reuses vanilla's Endermite segmented-body model (small, non-humanoid, already a
 * good fit for "compact tangle of cable segments") with our own texture, rather than
 * hand-building a new LayerDefinition from scratch.
 */
public class FragmentRenderer<T extends AbstractFragmentEntity> extends MobRenderer<T, LivingEntityRenderState, EndermiteModel> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("quantumvoid", "textures/entity/fragment.png");

    public FragmentRenderer(EntityRendererProvider.Context context) {
        super(context, new EndermiteModel(context.bakeLayer(ModelLayers.ENDERMITE)), 0.3f);
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEXTURE;
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }
}
