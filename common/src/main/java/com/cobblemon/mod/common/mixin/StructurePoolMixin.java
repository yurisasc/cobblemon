package com.cobblemon.mod.common.mixin;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.structure.pool.LegacySinglePoolElement;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(StructurePool.class)
public class StructurePoolMixin {
    Map<String, Integer> placedStructures;

    private static Map<String, Integer> structureMaxes;
    static {
        //Mapped using location string as key
        Map<String, Integer> aMap = new HashMap<String, Integer>();
        aMap.put("cobblemon:village_plains/village_plains_pokecenter", 1);
        aMap.put("cobblemon:village_savanna/village_savanna_pokecenter", 1);
        aMap.put("cobblemon:village_desert/village_desert_pokecenter", 1);
        aMap.put("cobblemon:village_snowy/village_snowy_pokecenter", 1);
        aMap.put("cobblemon:village_taiga/village_taiga_pokecenter", 1);
        structureMaxes = Collections.unmodifiableMap(aMap);
    }

    @Inject(method = "getElementIndicesInRandomOrder", at = @At("RETURN"))
    private void onGetElementIndicesInRandomOrder(Random random, CallbackInfoReturnable<List<StructurePoolElement>> cir) {
        placedStructures = new HashMap<String, Integer>();
        List<StructurePoolElement> structureList = cir.getReturnValue();
//        System.out.println("Randomised Pool (Before removing):");
//        System.out.println(structureList);

        ObjectArrayList<StructurePoolElement> reducedStructureList = new ObjectArrayList<StructurePoolElement>();
        for (StructurePoolElement structurePoolElement : structureList) {
            String location = getLocationIfAvailable(structurePoolElement);
            if (location == null) {
                reducedStructureList.add(structurePoolElement);
                continue;
            }

            Integer maxAllowed = structureMaxes.get(location);
            if (maxAllowed == null) {
                reducedStructureList.add(structurePoolElement);
                continue;
            }

            Integer currentlyPlaced = placedStructures.get(location);
            if (currentlyPlaced == null) {
                reducedStructureList.add(structurePoolElement);
                placedStructures.put(location, 1);
                continue;
            }

            if (currentlyPlaced < maxAllowed) {
                reducedStructureList.add(structurePoolElement);
                placedStructures.put(location, currentlyPlaced + 1);
            }
        }

//        System.out.println("AFTER REMOVING:");
//        System.out.println(reducedStructureList + "\n");
        try {
            cir.setReturnValue(reducedStructureList);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private String getLocationIfAvailable(StructurePoolElement structurePoolElement) {
        if (structurePoolElement instanceof SinglePoolElement) {
            SinglePoolElement singlePoolElement = (SinglePoolElement) structurePoolElement;
            if (singlePoolElement.location.left() == null) {
                return null;
            }

            return singlePoolElement.location.left().get().toString();
        } else if (structurePoolElement instanceof LegacySinglePoolElement) {
            LegacySinglePoolElement legacySinglePoolElement = (LegacySinglePoolElement) structurePoolElement;
            if (legacySinglePoolElement.location.left() == null) {
                return null;
            }

            return legacySinglePoolElement.location.left().get().toString();
        } else {
            return null;
        }
    }
}
