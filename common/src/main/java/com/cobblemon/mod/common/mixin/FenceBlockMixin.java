/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.mixin;

import com.cobblemon.mod.common.CobblemonBlocks;
import com.cobblemon.mod.common.block.PastureBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = FenceBlock.class)
public abstract class FenceBlockMixin {

    // This could be done with a modify variable but it just doesn't seem to want to work, very crude unfinished explanation I had on Fabric discord was
    // "boolean is a primitive, it isnt passed by reference but by value and you have your own object references anyway so assigning a new object to non-primitive references (variables) wont modify it after your mixin returns
    //(you cant modify a random argument that mixin passes you that way, target the boolean not the FenceBlock)"
    // However the issue is the only way to get all the params which we need is by passing in the entire method reference which enforces the return type.

    @Redirect(method = "getStateForNeighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/FenceBlock;canConnect(Lnet/minecraft/block/BlockState;ZLnet/minecraft/util/math/Direction;)Z"))
    private boolean cobblemon$getStateForNeighborUpdate(FenceBlock instance, BlockState state, boolean neighborIsFullSquare, Direction dir) {
        return instance.canConnect(state, neighborIsFullSquare, dir) || this.cobblemon$canConnect(state, dir);
    }

    @Redirect(method = "getPlacementState", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/FenceBlock;canConnect(Lnet/minecraft/block/BlockState;ZLnet/minecraft/util/math/Direction;)Z"))
    private boolean cobblemon$getPlacementState(FenceBlock instance, BlockState state, boolean neighborIsFullSquare, Direction dir) {
        return instance.canConnect(state, neighborIsFullSquare, dir) || this.cobblemon$canConnect(state, dir);
    }

    private boolean cobblemon$canConnect(BlockState state, Direction dir) {
        if (state.isOf(CobblemonBlocks.PASTURE) && state.get(PastureBlock.Companion.getPART()) == PastureBlock.PasturePart.BOTTOM) {
            final Direction pastureFacing = state.get(HorizontalFacingBlock.FACING);
            return dir == pastureFacing.rotateYClockwise() || dir == pastureFacing.rotateYCounterclockwise();
        }
        return false;
    }

}
