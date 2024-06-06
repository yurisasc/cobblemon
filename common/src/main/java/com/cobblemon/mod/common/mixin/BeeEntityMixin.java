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
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;


@Mixin(targets="net.minecraft.entity.passive.BeeEntity$GrowCropsGoal")
public abstract class BeeEntityMixin {

    @Unique
    private BlockState cobblemon$result = null;

    @Inject(method = "tick()V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/Fertilizable;grow(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/random/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V",
            shift = At.Shift.BY,
            by = 2
        ),
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void injectCustomGrowth(CallbackInfo ci, int i, BlockPos blockPos, BlockState blockState, Block block) {
        if (block instanceof SaccharineLeafBlock) {
            int age = blockState.get(SaccharineLeafBlock.Companion.getAGE());
            if (age < 2) {
                this.cobblemon$result = blockState.with(SaccharineLeafBlock.Companion.getAGE(), age + 1);
            }
        }
    }

    @ModifyVariable(method = "tick", at = @At(value = "LOAD", ordinal = 0), index = 5)
    private BlockState applyCustomBlockState(BlockState before) {
        if(this.cobblemon$result != null && before == null) {
            BlockState result = this.cobblemon$result;
            this.cobblemon$result = null;
            return result;
        }

        return before;
    }
}
