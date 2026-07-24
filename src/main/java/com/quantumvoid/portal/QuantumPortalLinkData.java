package com.quantumvoid.portal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import com.quantumvoid.QuantumVoid;

import net.minecraft.core.GlobalPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Persisted backing store for {@link QuantumPortalLinkRegistry} — always kept on the overworld's data storage, regardless of which dimension a link was made in. */
public class QuantumPortalLinkData extends SavedData {
    private record LinkEntry(long linkId, GlobalPos pos) {
        static final Codec<LinkEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.LONG.fieldOf("link_id").forGetter(LinkEntry::linkId),
                GlobalPos.CODEC.fieldOf("pos").forGetter(LinkEntry::pos)
        ).apply(instance, LinkEntry::new));
    }

    private static final Codec<Map<Long, GlobalPos>> LINKS_CODEC = Codec.list(LinkEntry.CODEC).xmap(
            list -> {
                Map<Long, GlobalPos> map = new HashMap<>();
                for (LinkEntry entry : list) {
                    map.put(entry.linkId(), entry.pos());
                }
                return map;
            },
            map -> map.entrySet().stream().map(e -> new LinkEntry(e.getKey(), e.getValue())).toList());

    public static final Codec<QuantumPortalLinkData> CODEC = LINKS_CODEC.xmap(QuantumPortalLinkData::new, data -> data.links);

    public static final SavedDataType<QuantumPortalLinkData> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath(QuantumVoid.MODID, "portal_links"),
            QuantumPortalLinkData::new,
            CODEC,
            DataFixTypes.LEVEL);

    private final Map<Long, GlobalPos> links;

    private QuantumPortalLinkData() {
        this(new HashMap<>());
    }

    private QuantumPortalLinkData(Map<Long, GlobalPos> links) {
        this.links = links;
    }

    Map<Long, GlobalPos> links() {
        return this.links;
    }

    void put(long linkId, GlobalPos pos) {
        this.links.put(linkId, pos);
        this.setDirty();
    }
}
