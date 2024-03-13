package com.cobblemon.mod.common.mixin.structure;

import com.cobblemon.mod.common.Cobblemon;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StructureTemplate.class) // this probably removes spawns for all(!!) structures so probably needs some work
public class StructureTemplateMixin {
    @Inject(method = "spawnEntities", at = @At(value = "HEAD"), cancellable = true)
    private void cobblemon$cancelStructureSpawns(ServerWorldAccess world, BlockPos pos, BlockMirror mirror, BlockRotation rotation, BlockPos pivot, BlockBox area, boolean initializeMobs, CallbackInfo ci) {
        if (!Cobblemon.config.getDoVanillaSpawns()) {
            ci.cancel();
        }
    }
}
