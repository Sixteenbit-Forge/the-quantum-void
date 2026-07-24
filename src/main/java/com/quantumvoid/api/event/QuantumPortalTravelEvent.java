package com.quantumvoid.api.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Fired after an entity successfully teleports through a Quantum Portal, once the destination
 * level and landing position are known. Fired from the base mod itself (not an addon) —
 * available from day one for any addon to subscribe to without depending on this being the
 * mod that eventually adds portal-transition perks (see docs/DESIGN.md tool passives, e.g.
 * "immune to portal-transition fall damage").
 */
public class QuantumPortalTravelEvent extends Event {
    private final Entity entity;
    private final ServerLevel from;
    private final ServerLevel to;
    private final boolean enteringQuantumVoid;

    public QuantumPortalTravelEvent(Entity entity, ServerLevel from, ServerLevel to, boolean enteringQuantumVoid) {
        this.entity = entity;
        this.from = from;
        this.to = to;
        this.enteringQuantumVoid = enteringQuantumVoid;
    }

    public Entity entity() {
        return entity;
    }

    public ServerLevel from() {
        return from;
    }

    public ServerLevel to() {
        return to;
    }

    /** True if this trip is entering the Quantum Void, false if returning to the origin dimension. */
    public boolean enteringQuantumVoid() {
        return enteringQuantumVoid;
    }

    public static void post(Entity entity, ServerLevel from, ServerLevel to, boolean enteringQuantumVoid) {
        NeoForge.EVENT_BUS.post(new QuantumPortalTravelEvent(entity, from, to, enteringQuantumVoid));
    }
}
