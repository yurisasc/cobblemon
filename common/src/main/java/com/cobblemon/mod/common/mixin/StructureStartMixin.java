package com.cobblemon.mod.common.mixin;

import net.minecraft.registry.Registry;
import net.minecraft.structure.StructurePiecesList;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.List;

@Mixin(StructureStart.class)
public class StructureStartMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onStructurePoolGeneratorCreation(Structure structure, ChunkPos pos, int references, StructurePiecesList children, CallbackInfo ci) {
        System.out.println("StructureStart Start:");
        System.out.println(structure);
        System.out.println(children);
        System.out.println("StructureStart END:\n");
    }
}
