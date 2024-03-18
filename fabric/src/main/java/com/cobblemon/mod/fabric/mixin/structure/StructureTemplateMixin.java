/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.fabric.mixin.structure;

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

@Mixin(StructureTemplate.class) // this probably removes spawns for all(!!) structures so probably needs some work
public class StructureTemplateMixin {
    @Inject(method = "method_17917", at = @At(value = "HEAD"), cancellable = true)
    private static void cobblemon$cancelStructureSpawns(BlockRotation blockRotation, BlockMirror blockMirror, Vec3d vec3d, boolean bl, ServerWorldAccess serverWorldAccess, NbtCompound nbtCompound, Entity entity, CallbackInfo ci) {
        if (!Cobblemon.config.getDoVanillaSpawns() && entity instanceof MobEntity) {
            ci.cancel();
        }
    }
}
