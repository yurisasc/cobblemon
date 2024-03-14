package com.cobblemon.mod.common.mixin.structure;

import com.cobblemon.mod.common.Cobblemon;
import net.minecraft.structure.SwampHutGenerator;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SwampHutGenerator.class)
public class SwampHutGeneratorMixin {
    @Inject(method = "generate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityType;create(Lnet/minecraft/world/World;)Lnet/minecraft/entity/Entity;"), cancellable = true)
    private void cobblemon$cancelWitchSpawn(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot, CallbackInfo ci) {
        if (!Cobblemon.config.getDoVanillaSpawns()) {
            ci.cancel();
        }
    }

    @Inject(method = "spawnCat", at = @At(value = "HEAD"), cancellable = true)
    private void cobblemon$cancelCatSpawn(ServerWorldAccess world, BlockBox box, CallbackInfo ci) {
        if (!Cobblemon.config.getDoVanillaSpawns()) {
            ci.cancel();
        }
    }
}