/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.world.block.entity

import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.api.berry.Berries
import com.cobblemon.mod.common.api.berry.Berry
import com.cobblemon.mod.common.world.block.BerryBlock
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtString
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.InvalidIdentifierException
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class BerryBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(CobblemonBlockEntities.BERRY.get(), pos, state) {

    var lifeCycles: Int = this.berry()?.lifeCycles?.random() ?: 1
        private set

    private val growthPoints = arrayListOf<Berry>()

    fun berryBlock() = this.cachedState.block as BerryBlock

    fun berry() = this.berryBlock().berry()

    /**
     * TODO
     *
     */
    fun generateGrowthPoints(world: World, state: BlockState, pos: BlockPos, player: ServerPlayerEntity?) {
        val berry = this.berry() ?: return
        val yield = berry.calculateYield(world, state, pos, player)
        this.growthPoints.clear()
        repeat(yield) {
            this.growthPoints += berry
        }
    }

    /**
     * TODO
     *
     * @param world
     * @param state
     * @param pos
     * @param player
     * @return
     */
    fun harvest(world: World, state: BlockState, pos: BlockPos, player: ServerPlayerEntity?): Collection<ItemStack> {
        TODO("Not yet implemented")
    }

    /**
     * TODO
     *
     * @param newCycles
     */
    fun setLifeCycles(newCycles: Int) {
        TODO("Not yet implemented")
    }

    override fun readNbt(nbt: NbtCompound) {
        nbt.putInt(LIFE_CYCLES, this.lifeCycles)
        val list = NbtList()
        list += this.growthPoints.map { NbtString.of(it.identifier.toString()) }
        nbt.put(GROWTH_POINTS, list)
    }

    override fun writeNbt(nbt: NbtCompound) {
        this.growthPoints.clear()
        this.lifeCycles = nbt.getInt(LIFE_CYCLES)
        nbt.getList(GROWTH_POINTS, NbtList.STRING_TYPE.toInt()).filterIsInstance<NbtString>().forEach { element ->
            try {
                val identifier = Identifier(element.asString())
                Berries.getByIdentifier(identifier)?.let { this.growthPoints.add(it) }
            } catch (ignored: InvalidIdentifierException) {}
        }
    }

    private fun consumeLife(world: World, pos: BlockPos) {
        if (--this.lifeCycles <= 0) {
            world.setBlockState(pos, Blocks.AIR.defaultState)
            return
        }
        this.markDirty()
    }

    companion object {

        private const val LIFE_CYCLES = "life_cycles"
        private const val GROWTH_POINTS = "growth_points"

    }

}