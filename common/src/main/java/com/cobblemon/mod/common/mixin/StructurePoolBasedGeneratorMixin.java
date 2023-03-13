package com.cobblemon.mod.common.mixin;

import net.minecraft.registry.Registry;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.pool.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.noise.NoiseConfig;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(StructurePoolBasedGenerator.class)
public class StructurePoolBasedGeneratorMixin {



//    @Inject(method = "generate", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
//    private static void onGenerate(Structure.Context context, RegistryEntry<StructurePool> structurePool, Optional<Identifier> id, int size, BlockPos pos, boolean useExpansionHack, Optional<Heightmap.Type> projectStartToHeightmap, int maxDistanceFromCenter, CallbackInfoReturnable<Optional<Structure.StructurePosition>> cir, StructurePoolElement structurePoolElement) {
//        // mixin code goes here
//        System.out.println("StructurePoolBasedGenerator.generate 1 method has been called!");
//        System.out.println(structurePoolElement);
//    }

//    @Inject( method = "generate(Lnet/minecraft/world/gen/noise/NoiseConfig;IZLnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/structure/StructureTemplateManager;Lnet/minecraft/world/HeightLimitView;Lnet/minecraft/util/math/random/Random;Lnet/minecraft/registry/Registry;Lnet/minecraft/structure/PoolStructurePiece;Ljava/util/List;Lnet/minecraft/util/shape/VoxelShape;)V", at = @At("HEAD"))
//    private static void onGenerate(NoiseConfig noiseConfig, int maxSize, boolean modifyBoundingBox, ChunkGenerator chunkGenerator, StructureTemplateManager structureTemplateManager, HeightLimitView heightLimitView, Random random, Registry<StructurePool> structurePoolRegistry, PoolStructurePiece firstPiece, List<PoolStructurePiece> pieces, VoxelShape pieceShape, CallbackInfo ci) {
//        // mixin code goes here
//        System.out.println("StructurePoolBasedGenerator.generate 2 method has been called START!");
//        System.out.println(structurePoolRegistry);
//        System.out.println(pieces);
//
//
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

//    @Inject( method = "generate(Lnet/minecraft/world/gen/noise/NoiseConfig;IZLnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/structure/StructureTemplateManager;Lnet/minecraft/world/HeightLimitView;Lnet/minecraft/util/math/random/Random;Lnet/minecraft/registry/Registry;Lnet/minecraft/structure/PoolStructurePiece;Ljava/util/List;Lnet/minecraft/util/shape/VoxelShape;)V", at = @At("HEAD"))
//    private static void onGenerate(NoiseConfig noiseConfig, int maxSize, boolean modifyBoundingBox, ChunkGenerator chunkGenerator, StructureTemplateManager structureTemplateManager, HeightLimitView heightLimitView, Random random, Registry<StructurePool> structurePoolRegistry, PoolStructurePiece firstPiece, List<PoolStructurePiece> pieces, VoxelShape pieceShape, CallbackInfo ci) {
//        // mixin code goes here
//        System.out.println("StructurePoolBasedGenerator.generate 2 method has been called START!");
//        System.out.println(structurePoolRegistry);
//        System.out.println(pieces);
//
//
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
//
//
//    }
}
