package com.quantumvoid.client;

import com.quantumvoid.QuantumVoid;
import com.quantumvoid.boss.client.FracturedCoreRenderer;
import com.quantumvoid.entity.client.FragmentRenderer;

import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = QuantumVoid.MODID, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(QuantumVoid.FRAGMENT_MELEE.get(), FragmentRenderer::new);
        event.registerEntityRenderer(QuantumVoid.FRAGMENT_RANGED.get(), FragmentRenderer::new);
        event.registerEntityRenderer(QuantumVoid.FRAGMENT_BOLT.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(QuantumVoid.FRACTURED_CORE.get(), FracturedCoreRenderer::new);
    }
}
