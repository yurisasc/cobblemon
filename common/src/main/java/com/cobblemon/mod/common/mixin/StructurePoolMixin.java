package com.cobblemon.mod.common.mixin;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.util.Util;

import java.util.*;

@Mixin(StructurePool.class)
public class StructurePoolMixin {
    @Shadow ObjectArrayList<StructurePoolElement> elements;
    Map<String, Integer> placedStructures;

    private static Map<String, Integer> structureMaxes;
    static {
        //Mapped using location string as key
        Map<String, Integer> aMap = new HashMap<String, Integer>();
        aMap.put("cobblemon:village_plains_pokecenter", 1);
        structureMaxes = Collections.unmodifiableMap(aMap);
    }

    private ArrayList<StructurePoolElement> generatedStructures = new ArrayList<StructurePoolElement>();

    @Inject(method = "getRandomElement", at = @At("RETURN"))
    private void onGetRandomElement(Random random, CallbackInfoReturnable<StructurePoolElement> cir) {
        // mixin code goes here
        System.out.println("StructurePoolBasedGenerator.generate 1 method has been called START!");
        System.out.println(this.elements);
        System.out.println(cir.getReturnValue().getType());
        System.out.println(cir.getReturnValue().toString());
        System.out.println(cir.getReturnValue());
        generatedStructures.add(cir.getReturnValue());
        System.out.println(generatedStructures);
        System.out.println("StructurePoolBasedGenerator.generate 1 method has been called END!");
    }

    @Inject(method = "getElementIndicesInRandomOrder", at = @At("RETURN"))
    private void onGetElementIndicesInRandomOrder(Random random, CallbackInfoReturnable<List<StructurePoolElement>> cir) {
        placedStructures = new HashMap<String, Integer>();
        System.out.println("Randomised Pool (Before removing):");
        List<StructurePoolElement> structureList = cir.getReturnValue();
        System.out.println(structureList);

        ObjectArrayList<StructurePoolElement> reducedStructureList = new ObjectArrayList<StructurePoolElement>();
        for (StructurePoolElement structurePoolElement: structureList) {
            if (structurePoolElement instanceof  SinglePoolElement) {
                SinglePoolElement singlePoolElement = (SinglePoolElement) structurePoolElement;
                if (singlePoolElement.location.left() == null) {
                    reducedStructureList.add(structurePoolElement);
                    continue;
                }

                String location = singlePoolElement.location.left().get().toString();
                System.out.println(location);

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
                    placedStructures.put(location, currentlyPlaced+1);
                }
            } else {
                reducedStructureList.add(structurePoolElement);
            }
        }

        System.out.println("AFTER REMOVING:");
        System.out.println(reducedStructureList);
        try {
            cir.setReturnValue(reducedStructureList);
//        cir.setReturnValue(Util.copyShuffled(reducedStructureList, random));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

//    @Inject(method = "getElementIndicesInRandomOrder", at = @At("RETURN"))
//    private void onGetElementIndicesInRandomOrder(Random random, CallbackInfoReturnable<List<StructurePoolElement>> cir) {
//        placedStructures = new HashMap<String, Integer>();
//        System.out.println("Randomised Pool (Before removing):");
//        List<StructurePoolElement> structureList = cir.getReturnValue();
//        System.out.println(structureList);
//
//        for (StructurePoolElement structurePoolElement: structureList) {
//            if (structurePoolElement instanceof  SinglePoolElement) {
//                SinglePoolElement singlePoolElement = (SinglePoolElement) structurePoolElement;
//                if (singlePoolElement.location.left() == null) {
//                    continue;
//                }
//
//                String location = singlePoolElement.location.left().get().toString();
//                System.out.println(location);
//
//                Integer maxAllowed = structureMaxes.get(location);
//                if (maxAllowed == null) {
//                    continue;
//                }
//
//                Integer currentlyPlaced = placedStructures.get(location);
//                if (currentlyPlaced == null) {
//                    placedStructures.put(location, 1);
//                    continue;
//                }
//
//                if (currentlyPlaced < maxAllowed) {
//                    placedStructures.put(location, currentlyPlaced+1);
//                } else {
//                    structureList.remove(structurePoolElement);
//                }
//            }
//        }
//
//        System.out.println("AFTER REMOVING:");
//        System.out.println(structureList);
//        try {
//            cir.setReturnValue(structureList);
//        } catch (Exception e) {
//            System.out.println(e);
//        }
//
//    }
}
