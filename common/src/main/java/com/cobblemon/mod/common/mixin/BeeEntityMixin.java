/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.mixin;

import com.cobblemon.mod.common.block.SaccharineLeafBlock;
import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(targets="net.minecraft.entity.passive.BeeEntity$GrowCropsGoal")
public abstract class BeeEntityGrowCropsGoalMixin {
    @Inject(
            method = "tick()V",
            at = @At("TAIL"),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/block/Fertilizable;grow(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/random/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V")
            )
    )
    private void injectCustomGrowth(CallbackInfo ci) {
        BeeEntity bee = (BeeEntity) (Object) this;
        BlockPos blockPos = bee.getBlockPos().down();
        BlockState blockState = bee.getWorld().getBlockState(blockPos);
        Block block = blockState.getBlock();

        if (block instanceof SaccharineLeafBlock) {
            int age = blockState.get(SaccharineLeafBlock.Companion.getAGE());
            if (age < 2) {
                BlockState newState = blockState.with(SaccharineLeafBlock.Companion.getAGE(), age + 1);
                bee.getWorld().setBlockState(blockPos, newState);
            }
        }
    }
}
