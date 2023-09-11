/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */


package com.cobblemon.mod.common.mixin;

import com.cobblemon.mod.common.block.PreEmptsExplosion;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = Explosion.class)
public class ExplosionMixin {

    @Shadow @Final private World world;

    @Inject(
            method = "affectWorld",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;shouldDropItemsOnExplosion(Lnet/minecraft/world/explosion/Explosion;)Z"),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    public void cobblemon$whenExploded(boolean particles, CallbackInfo ci, boolean bl, ObjectArrayList objectArrayList, boolean bl2, ObjectListIterator var5, BlockPos blockPos, BlockState blockState, Block block, BlockPos blockPos2) {
        if (block instanceof PreEmptsExplosion preExplosionBlock) {
            preExplosionBlock.whenExploded(world, blockState, blockPos2);
        }
    }
}
