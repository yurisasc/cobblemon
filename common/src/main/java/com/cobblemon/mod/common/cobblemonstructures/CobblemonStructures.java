package com.cobblemon.mod.common.cobblemonstructures;

import com.cobblemon.mod.common.mixin.StructurePoolAccessor;
import com.mojang.datafixers.util.Pair;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.pool.LegacySinglePoolElement;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class CobblemonStructures {

    private static final RegistryKey<StructureProcessorList> EMPTY_PROCESSOR_LIST_KEY = RegistryKey.of(RegistryKeys.PROCESSOR_LIST, new Identifier("minecraft", "empty"));
    private static final Integer pokecenterWeight = 75;
    private static final Integer longPathWeight = 10;

    public static void registerJigsaws(MinecraftServer server) {
        Registry<StructurePool> templatePoolRegistry = server.getRegistryManager().get(RegistryKeys.TEMPLATE_POOL);
        Registry<StructureProcessorList> processorListRegistry = server.getRegistryManager().get(RegistryKeys.PROCESSOR_LIST);

        addPokecenters(templatePoolRegistry, processorListRegistry);
        addLongPaths(templatePoolRegistry, processorListRegistry);
    }

    private static void addPokecenters(Registry<StructurePool> templatePoolRegistry, Registry<StructureProcessorList> processorListRegistry) {
        Identifier plainsHousesPoolLocation = new Identifier("minecraft:village/plains/houses");
        Identifier desertHousesPoolLocation = new Identifier("minecraft:village/desert/houses");
        Identifier savannaHousesPoolLocation = new Identifier("minecraft:village/savanna/houses");
        Identifier snowyHousesPoolLocation = new Identifier("minecraft:village/snowy/houses");
        Identifier taigaHousesPoolLocation = new Identifier("minecraft:village/taiga/houses");

        addBuildingToPool(templatePoolRegistry, processorListRegistry, plainsHousesPoolLocation, CobblemonStructureIDs.PLAINS_POKECENTER, pokecenterWeight, StructurePool.Projection.RIGID, EMPTY_PROCESSOR_LIST_KEY);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, desertHousesPoolLocation, CobblemonStructureIDs.DESERT_POKECENTER, pokecenterWeight, StructurePool.Projection.RIGID, EMPTY_PROCESSOR_LIST_KEY);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, savannaHousesPoolLocation, CobblemonStructureIDs.SAVANNA_POKECENTER, pokecenterWeight, StructurePool.Projection.RIGID, EMPTY_PROCESSOR_LIST_KEY);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, snowyHousesPoolLocation, CobblemonStructureIDs.SNOWY_POKECENTER, pokecenterWeight, StructurePool.Projection.RIGID, EMPTY_PROCESSOR_LIST_KEY);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, taigaHousesPoolLocation, CobblemonStructureIDs.TAIGA_POKECENTER, pokecenterWeight, StructurePool.Projection.RIGID, StructureProcessorLists.MOSSIFY_10_PERCENT);
    }
    private static void addLongPaths(Registry<StructurePool> templatePoolRegistry, Registry<StructureProcessorList> processorListRegistry) {
        Identifier plainsStreetsPoolLocation = new Identifier("minecraft:village/plains/streets");
        Identifier desertStreetsPoolLocation = new Identifier("minecraft:village/desert/streets");
        Identifier savannaStreetsPoolLocation = new Identifier("minecraft:village/savanna/streets");
        Identifier snowyStreetsPoolLocation = new Identifier("minecraft:village/snowy/streets");
        Identifier taigaStreetsPoolLocation = new Identifier("minecraft:village/taiga/streets");

        addLegacyBuildingToPool(templatePoolRegistry, processorListRegistry, plainsStreetsPoolLocation, CobblemonStructureIDs.PLAINS_LONG_PATH, longPathWeight, StructurePool.Projection.TERRAIN_MATCHING, StructureProcessorLists.STREET_PLAINS);
        addLegacyBuildingToPool(templatePoolRegistry, processorListRegistry, desertStreetsPoolLocation, CobblemonStructureIDs.DESERT_LONG_PATH, longPathWeight, StructurePool.Projection.TERRAIN_MATCHING, EMPTY_PROCESSOR_LIST_KEY);
        addLegacyBuildingToPool(templatePoolRegistry, processorListRegistry, savannaStreetsPoolLocation, CobblemonStructureIDs.SAVANNA_LONG_PATH, longPathWeight, StructurePool.Projection.TERRAIN_MATCHING, StructureProcessorLists.STREET_SAVANNA);
        addLegacyBuildingToPool(templatePoolRegistry, processorListRegistry, snowyStreetsPoolLocation, CobblemonStructureIDs.SNOWY_LONG_PATH, longPathWeight, StructurePool.Projection.TERRAIN_MATCHING, StructureProcessorLists.STREET_SNOWY_OR_TAIGA);
        addLegacyBuildingToPool(templatePoolRegistry, processorListRegistry, taigaStreetsPoolLocation, CobblemonStructureIDs.TAIGA_LONG_PATH, longPathWeight, StructurePool.Projection.TERRAIN_MATCHING, StructureProcessorLists.STREET_SNOWY_OR_TAIGA);
    }

    public static void addLegacyBuildingToPool(Registry<StructurePool> templatePoolRegistry, Registry<StructureProcessorList> processorListRegistry, Identifier poolRL, String nbtPieceRL, int weight, StructurePool.Projection projection, RegistryKey<StructureProcessorList> processorListKey) {
        addBuildingToPool(templatePoolRegistry, processorListRegistry, poolRL, nbtPieceRL, weight, projection, processorListKey, true);
    }

    public static void addBuildingToPool(Registry<StructurePool> templatePoolRegistry, Registry<StructureProcessorList> processorListRegistry, Identifier poolRL, String nbtPieceRL, int weight, StructurePool.Projection projection, RegistryKey<StructureProcessorList> processorListKey) {
        addBuildingToPool(templatePoolRegistry, processorListRegistry, poolRL, nbtPieceRL, weight, projection, processorListKey, false);
    }

    public static void addBuildingToPool(Registry<StructurePool> templatePoolRegistry, Registry<StructureProcessorList> processorListRegistry, Identifier poolRL, String nbtPieceRL, int weight, StructurePool.Projection projection, RegistryKey<StructureProcessorList> processorListKey, boolean shouldUseLegacySingePoolElement) {
        if (processorListRegistry.getEntry(processorListKey).isEmpty()) return;
        RegistryEntry.Reference<StructureProcessorList> processorList = processorListRegistry.getEntry(processorListKey).get();

        StructurePool pool = templatePoolRegistry.get(poolRL);
        if (pool == null) return;

        SinglePoolElement piece;
        if (shouldUseLegacySingePoolElement) {
            piece = LegacySinglePoolElement.ofProcessedLegacySingle(nbtPieceRL, processorList).apply(projection);
        } else {
            piece = SinglePoolElement.ofProcessedSingle(nbtPieceRL, processorList).apply(projection);
        }

        for (int i = 0; i < weight; i++) {
            ((StructurePoolAccessor) pool).getElements().add(piece);
        }

        List<Pair<StructurePoolElement, Integer>> listOfPieceEntries = new ArrayList<>(((StructurePoolAccessor) pool).getElementCounts());
        listOfPieceEntries.add(new Pair<>(piece, weight));
        ((StructurePoolAccessor) pool).setElementCounts(listOfPieceEntries);
    }
}
