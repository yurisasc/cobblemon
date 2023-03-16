package com.cobblemon.mod.common.mixin;

import com.google.common.collect.Lists;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.pool.*;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.structure.Structure;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.*;

@Mixin(StructurePoolBasedGenerator.class)
public class StructurePoolBasedGeneratorMixin {

    private static Map<String, Integer> generatedStructureCounts;

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

    @Inject(method = "generate(Lnet/minecraft/world/gen/structure/Structure$Context;Lnet/minecraft/registry/entry/RegistryEntry;Ljava/util/Optional;ILnet/minecraft/util/math/BlockPos;ZLjava/util/Optional;I)Ljava/util/Optional;",
            at = @At("HEAD"))
    private static void onStartingGeneration(Structure.Context context, RegistryEntry<StructurePool> structurePool, Optional<Identifier> id, int size, BlockPos pos, boolean useExpansionHack, Optional<Heightmap.Type> projectStartToHeightmap, int maxDistanceFromCenter, CallbackInfoReturnable<Optional<Structure.StructurePosition>> cir) {
        System.out.println("Generation Started:");
        generatedStructureCounts = new HashMap<String, Integer>();
    }

//    @Inject(method = "generate(Lnet/minecraft/world/gen/structure/Structure$Context;Lnet/minecraft/registry/entry/RegistryEntry;Ljava/util/Optional;ILnet/minecraft/util/math/BlockPos;ZLjava/util/Optional;I)Ljava/util/Optional;",
//            at = @At("RETURN"),
//            cancellable = true)
//    private static void onFinishingGeneration(Structure.Context context, RegistryEntry<StructurePool> structurePool, Optional<Identifier> id, int size, BlockPos pos, boolean useExpansionHack, Optional<Heightmap.Type> projectStartToHeightmap, int maxDistanceFromCenter, CallbackInfoReturnable<Optional<Structure.StructurePosition>> cir) {
//        System.out.println("Generation Started:");
//        BlockPos blockPos;
//        DynamicRegistryManager dynamicRegistryManager = context.dynamicRegistryManager();
//        ChunkGenerator chunkGenerator = context.chunkGenerator();
//        StructureTemplateManager structureTemplateManager = context.structureTemplateManager();
//        HeightLimitView heightLimitView = context.world();
//        ChunkRandom chunkRandom = context.random();
//        Registry<StructurePool> registry = dynamicRegistryManager.get(RegistryKeys.TEMPLATE_POOL);
//        BlockRotation blockRotation = BlockRotation.random(chunkRandom);
//        StructurePool structurePool2 = structurePool.value();
//        StructurePoolElement structurePoolElement = structurePool2.getRandomElement(chunkRandom);
//        if (structurePoolElement == EmptyPoolElement.INSTANCE) {
//            cir.setReturnValue(Optional.empty());
//            return;
//        }
//        if (id.isPresent()) {
//            Identifier identifier = id.get();
//            Optional<BlockPos> optional = StructurePoolBasedGenerator.findStartingJigsawPos(structurePoolElement, identifier, pos, blockRotation, structureTemplateManager, chunkRandom);
//            if (optional.isEmpty()) {
////                LOGGER.error("No starting jigsaw {} found in start pool {}", (Object)identifier, (Object)structurePool.getKey().map(key -> key.getValue().toString()).orElse("<unregistered>"));
//                cir.setReturnValue(Optional.empty());
//                return;
//            }
//            blockPos = optional.get();
//        } else {
//            blockPos = pos;
//        }
//        BlockPos vec3i = blockPos.subtract(pos);
//        BlockPos blockPos2 = pos.subtract(vec3i);
//        PoolStructurePiece poolStructurePiece = new PoolStructurePiece(structureTemplateManager, structurePoolElement, blockPos2, structurePoolElement.getGroundLevelDelta(), blockRotation, structurePoolElement.getBoundingBox(structureTemplateManager, blockPos2, blockRotation));
//        BlockBox blockBox = poolStructurePiece.getBoundingBox();
//        int i = (blockBox.getMaxX() + blockBox.getMinX()) / 2;
//        int j = (blockBox.getMaxZ() + blockBox.getMinZ()) / 2;
//        int k = projectStartToHeightmap.isPresent() ? pos.getY() + chunkGenerator.getHeightOnGround(i, j, projectStartToHeightmap.get(), heightLimitView, context.noiseConfig()) : blockPos2.getY();
//        int l = blockBox.getMinY() + poolStructurePiece.getGroundLevelDelta();
//        poolStructurePiece.translate(0, k - l, 0);
//        int m = k + vec3i.getY();
//        Optional<Structure.StructurePosition> optional = Optional.of(new Structure.StructurePosition(new BlockPos(i, m, j), collector -> {
//            ArrayList<PoolStructurePiece> list = Lists.newArrayList();
//            list.add(poolStructurePiece);
//            if (size <= 0) {
//                return;
//            }
//            System.out.println(list);
//            Box box = new Box(i - maxDistanceFromCenter, m - maxDistanceFromCenter, j - maxDistanceFromCenter, i + maxDistanceFromCenter + 1, m + maxDistanceFromCenter + 1, j + maxDistanceFromCenter + 1);
//            VoxelShape voxelShape = VoxelShapes.combineAndSimplify(VoxelShapes.cuboid(box), VoxelShapes.cuboid(Box.from(blockBox)), BooleanBiFunction.ONLY_FIRST);
//            System.out.println("Doing stuff here");
//            StructurePoolBasedGenerator.generate(context.noiseConfig(), size, useExpansionHack, chunkGenerator, structureTemplateManager, heightLimitView, chunkRandom, registry, poolStructurePiece, list, voxelShape);
//            System.out.println(list);
//            list.forEach(collector::addPiece);
//            System.out.println(list.size());
//            System.out.println(generatedStructureCounts);
//        }));
//
//        cir.setReturnValue(optional);
//    }

//    @Inject( method = "generate(Lnet/minecraft/world/gen/noise/NoiseConfig;IZLnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/structure/StructureTemplateManager;Lnet/minecraft/world/HeightLimitView;Lnet/minecraft/util/math/random/Random;Lnet/minecraft/registry/Registry;Lnet/minecraft/structure/PoolStructurePiece;Ljava/util/List;Lnet/minecraft/util/shape/VoxelShape;)V",
//            at = @At("HEAD"))
//    private static void onGenerate(NoiseConfig noiseConfig, int maxSize, boolean modifyBoundingBox, ChunkGenerator chunkGenerator, StructureTemplateManager structureTemplateManager, HeightLimitView heightLimitView, Random random, Registry<StructurePool> structurePoolRegistry, PoolStructurePiece firstPiece, List<PoolStructurePiece> pieces, VoxelShape pieceShape, CallbackInfo ci) {
//        StructurePoolBasedGenerator.StructurePoolGenerator structurePoolGenerator = new StructurePoolBasedGenerator.StructurePoolGenerator(structurePoolRegistry, maxSize, chunkGenerator, structureTemplateManager, pieces, random);
//        System.out.println(structurePoolGenerator.structurePieces);
//        for (StructurePoolBasedGenerator.ShapedPoolStructurePiece piece: structurePoolGenerator.structurePieces) {
//            System.out.println(piece.piece.getPoolElement());
//        }
//        structurePoolGenerator.structurePieces.addLast(new StructurePoolBasedGenerator.ShapedPoolStructurePiece(firstPiece, new MutableObject(pieceShape), 0));
//        System.out.println(structurePoolGenerator.structurePieces);
//        for (StructurePoolBasedGenerator.ShapedPoolStructurePiece piece: structurePoolGenerator.structurePieces) {
//            System.out.println(piece.piece.getPoolElement());
//        }
//
//        while(!structurePoolGenerator.structurePieces.isEmpty()) {
//            StructurePoolBasedGenerator.ShapedPoolStructurePiece shapedPoolStructurePiece = (StructurePoolBasedGenerator.ShapedPoolStructurePiece)structurePoolGenerator.structurePieces.removeFirst();
//            System.out.println("Now generating: ");
//            System.out.println(shapedPoolStructurePiece.piece.getPoolElement());
//            System.out.println(shapedPoolStructurePiece.piece.getPoolElement().CODEC);
//
//            System.out.println(shapedPoolStructurePiece.piece.getPoolElement().toString());
//            System.out.println(shapedPoolStructurePiece.piece.getPoolElement().getType());
//            System.out.println("Pieces remaining: ");
//            System.out.println(structurePoolGenerator.structurePieces);
//            for (StructurePoolBasedGenerator.ShapedPoolStructurePiece piece: structurePoolGenerator.structurePieces) {
//                System.out.println(piece.piece.getPoolElement());
//            }
//            structurePoolGenerator.generatePiece(shapedPoolStructurePiece.piece, shapedPoolStructurePiece.pieceShape, shapedPoolStructurePiece.currentSize, modifyBoundingBox, heightLimitView, noiseConfig);
//        }
//
//        System.out.println("StructurePoolBasedGenerator.generate 2 method has been called END!");
//    }

    @Inject(method = "generate(Lnet/minecraft/world/gen/noise/NoiseConfig;IZLnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/structure/StructureTemplateManager;Lnet/minecraft/world/HeightLimitView;Lnet/minecraft/util/math/random/Random;Lnet/minecraft/registry/Registry;Lnet/minecraft/structure/PoolStructurePiece;Ljava/util/List;Lnet/minecraft/util/shape/VoxelShape;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/structure/pool/StructurePoolBasedGenerator$StructurePoolGenerator;generatePiece(Lnet/minecraft/structure/PoolStructurePiece;Lorg/apache/commons/lang3/mutable/MutableObject;IZLnet/minecraft/world/HeightLimitView;Lnet/minecraft/world/gen/noise/NoiseConfig;)V",
                    shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void onGeneratePiece(NoiseConfig noiseConfig, int maxSize, boolean modifyBoundingBox, ChunkGenerator chunkGenerator, StructureTemplateManager structureTemplateManager, HeightLimitView heightLimitView, Random random, Registry<StructurePool> structurePoolRegistry, PoolStructurePiece firstPiece, List<PoolStructurePiece> pieces, VoxelShape pieceShape, CallbackInfo ci, StructurePoolBasedGenerator.StructurePoolGenerator structurePoolGenerator, StructurePoolBasedGenerator.ShapedPoolStructurePiece shapedPoolStructurePiece) {
        StructurePoolElement generatedPiece = shapedPoolStructurePiece.piece.getPoolElement();
        String location = getLocationIfAvailable(generatedPiece);
        if (location == null) {
            return;
        }

        Integer currentlyPlacedCount = generatedStructureCounts.get(location);
        if (currentlyPlacedCount == null) {
            generatedStructureCounts.put(location, 1);
            return;
        }

        generatedStructureCounts.put(location, currentlyPlacedCount+1);
    }

    private static String getLocationIfAvailable(StructurePoolElement structurePoolElement) {
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

//    @Redirect(method = "generate(Lnet/minecraft/world/gen/noise/NoiseConfig;IZLnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/structure/StructureTemplateManager;Lnet/minecraft/world/HeightLimitView;Lnet/minecraft/util/math/random/Random;Lnet/minecraft/registry/Registry;Lnet/minecraft/structure/PoolStructurePiece;Ljava/util/List;Lnet/minecraft/util/shape/VoxelShape;)V",
//    at = @At(value = "INVOKE", target = "Ljava/util/Deque;removeFirst()Ljava/lang/Object;"))
//    private static void onGenerate(StructurePoolBasedGenerator.ShapedPoolStructurePiece instance) {
//        // mixin code goes here
//        System.out.println("StructurePoolBasedGenerator.generate 2 method has been called!");
//        System.out.println(structurePoolRegistry);
//        System.out.println(pieces);
//
//
//        StructurePoolBasedGenerator.StructurePoolGenerator structurePoolGenerator = new StructurePoolBasedGenerator.StructurePoolGenerator(structurePoolRegistry, maxSize, chunkGenerator, structureTemplateManager, pieces, random);
//    }
}