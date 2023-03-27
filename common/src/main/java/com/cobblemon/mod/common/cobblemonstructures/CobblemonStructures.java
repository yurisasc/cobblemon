package com.cobblemon.mod.common.cobblemonstructures;

import com.cobblemon.mod.common.mixin.StructurePoolAccessor;
import com.mojang.datafixers.util.Pair;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class CobblemonStructures {

    private static final RegistryKey<StructureProcessorList> EMPTY_PROCESSOR_LIST_KEY = RegistryKey.of(RegistryKeys.PROCESSOR_LIST, new Identifier("minecraft", "empty"));
    private static final RegistryKey<StructureProcessorList> MOSSIFY_10_PERCENT_PROCESSOR_LIST_KEY = RegistryKey.of(RegistryKeys.PROCESSOR_LIST, new Identifier("minecraft", "mossify_10_percent"));
    private static final Integer pokecenterWeight = 35;
    private static final Integer longPathWeight = 20;

    public static void registerJigsaws(MinecraftServer server) {
        Registry<StructurePool> templatePoolRegistry = server.getRegistryManager().get(RegistryKeys.TEMPLATE_POOL);
        Registry<StructureProcessorList> processorListRegistry = server.getRegistryManager().get(RegistryKeys.PROCESSOR_LIST);

        Identifier plainsHousesPoolLocation = new Identifier("minecraft:village/plains/houses");
        Identifier desertHousesPoolLocation = new Identifier("minecraft:village/desert/houses");
        Identifier savannaHousesPoolLocation = new Identifier("minecraft:village/savanna/houses");
        Identifier snowyHousesPoolLocation = new Identifier("minecraft:village/snowy/houses");
        Identifier taigaHousesPoolLocation = new Identifier("minecraft:village/taiga/houses");

        addBuildingToPool(templatePoolRegistry, processorListRegistry, plainsHousesPoolLocation, "cobblemon:village_plains/village_plains_pokecenter", pokecenterWeight, StructurePool.Projection.RIGID, EMPTY_PROCESSOR_LIST_KEY);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, desertHousesPoolLocation, "cobblemon:village_desert/village_desert_pokecenter", pokecenterWeight, StructurePool.Projection.RIGID, EMPTY_PROCESSOR_LIST_KEY);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, savannaHousesPoolLocation, "cobblemon:village_savanna/village_savanna_pokecenter", pokecenterWeight, StructurePool.Projection.RIGID, EMPTY_PROCESSOR_LIST_KEY);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, snowyHousesPoolLocation, "cobblemon:village_snowy/village_snowy_pokecenter", pokecenterWeight, StructurePool.Projection.RIGID, EMPTY_PROCESSOR_LIST_KEY);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, taigaHousesPoolLocation, "cobblemon:village_taiga/village_taiga_pokecenter", pokecenterWeight, StructurePool.Projection.RIGID, EMPTY_PROCESSOR_LIST_KEY);

//        Identifier plainsStreetsPoolLocation = new Identifier("minecraft:village/plains/streets");
//        Identifier desertStreetsPoolLocation = new Identifier("minecraft:village/desert/streets");
//        Identifier savannaStreetsPoolLocation = new Identifier("minecraft:village/savanna/streets");
//        Identifier snowyStreetsPoolLocation = new Identifier("minecraft:village/snowy/streets");
//        Identifier taigaStreetsPoolLocation = new Identifier("minecraft:village/taiga/streets");

//        addBuildingToPool(templatePoolRegistry, processorListRegistry, plainsStreetsPoolLocation, "cobblemon:village_desert/village_plains_long_path", longPathWeight, StructurePool.Projection.TERRAIN_MATCHING);
//        addBuildingToPool(templatePoolRegistry, processorListRegistry, desertStreetsPoolLocation, "cobblemon:village_desert/village_desert_long_path", longPathWeight, StructurePool.Projection.TERRAIN_MATCHING);
//        addBuildingToPool(templatePoolRegistry, processorListRegistry, savannaStreetsPoolLocation, "cobblemon:village_desert/village_savanna_long_path", longPathWeight, StructurePool.Projection.TERRAIN_MATCHING);
//        addBuildingToPool(templatePoolRegistry, processorListRegistry, snowyStreetsPoolLocation, "cobblemon:village_desert/village_snowy_long_path", longPathWeight, StructurePool.Projection.TERRAIN_MATCHING);
//        addBuildingToPool(templatePoolRegistry, processorListRegistry, taigaStreetsPoolLocation, "cobblemon:village_desert/village_taiga_long_path", longPathWeight, StructurePool.Projection.TERRAIN_MATCHING);
    }

    public static void addBuildingToPool(Registry<StructurePool> templatePoolRegistry, Registry<StructureProcessorList> processorListRegistry, Identifier poolRL, String nbtPieceRL, int weight, StructurePool.Projection projection, RegistryKey<StructureProcessorList> processorListKey) {
        if (processorListRegistry.getEntry(processorListKey).isEmpty()) return;
        RegistryEntry.Reference<StructureProcessorList> processorList = processorListRegistry.getEntry(processorListKey).get();

        StructurePool pool = templatePoolRegistry.get(poolRL);
        if (pool == null) return;

        SinglePoolElement piece = SinglePoolElement.ofProcessedSingle(nbtPieceRL, processorList).apply(projection);

        for (int i = 0; i < weight; i++) {
            ((StructurePoolAccessor) pool).getElements().add(piece);
        }

        List<Pair<StructurePoolElement, Integer>> listOfPieceEntries = new ArrayList<>(((StructurePoolAccessor) pool).getElementCounts());
        listOfPieceEntries.add(new Pair<>(piece, weight));
        ((StructurePoolAccessor) pool).setElementCounts(listOfPieceEntries);
    }
}
