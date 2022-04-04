package com.cablemc.pokemoncobbled.common.world.level.block.entity

import com.cablemc.pokemoncobbled.common.CobbledBlockEntities
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.state.BlockState
import java.util.*

class HealingMachineBlockEntity(
    blockPos: BlockPos,
    blockState: BlockState) : BlockEntity(CobbledBlockEntities.HEALING_MACHINE.get(), blockPos, blockState
) {
    private var currentUser: UUID? = null

    fun isInUse(): Boolean {
        return this.currentUser != null
    }

    fun setUser(user: UUID) {
        this.currentUser = user
    }

    companion object : BlockEntityTicker<HealingMachineBlockEntity> {
        override fun tick(level: Level, blockPos: BlockPos, blockState: BlockState, tileEntity: HealingMachineBlockEntity) {

        }
    }
}