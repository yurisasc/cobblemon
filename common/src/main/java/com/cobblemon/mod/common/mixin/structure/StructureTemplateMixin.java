package com.cobblemon.mod.common.mixin.structure;

import com.cobblemon.mod.common.Cobblemon;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(StructureTemplate.class) // this probably removes spawns for all(!!) structures so probably needs some work
public class StructureTemplateMixin {
    @Inject(method = "method_17917", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;applyRotation(Lnet/minecraft/util/BlockRotation;)F"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private static void cobblemon$cancelStructureSpawns(BlockRotation blockRotation, BlockMirror blockMirror, Vec3d vec3d, boolean bl, ServerWorldAccess serverWorldAccess, NbtCompound nbtCompound, Entity entity, CallbackInfo ci) {
        if (!Cobblemon.config.getDoVanillaSpawns() && entity instanceof MobEntity) {
            ci.cancel();
        }
    }
}
