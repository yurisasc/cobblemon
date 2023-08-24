package com.cobblemon.mod.common.block.multiblock

import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

interface MultiblockStructure {
    //For overriding
    fun isUsable(): Boolean = true
    fun onUse(
        blockState: BlockState,
        world: World,
        blockPos: BlockPos,
        player: PlayerEntity,
        interactionHand: Hand,
        blockHitResult: BlockHitResult
    ): ActionResult

    fun onBreak(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity?)

    fun tick()
}
