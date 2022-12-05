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
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.events.berry.BerryHarvestEvent
import com.cobblemon.mod.common.world.block.BerryBlock
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtString
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.InvalidIdentifierException
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent

class BerryBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(CobblemonBlockEntities.BERRY.get(), pos, state) {

    /**
     * The number of life cycles on this plant.
     * @throws IllegalArgumentException if the cycles are set to less than 0.
     */
    var lifeCycles: Int = this.berry()?.lifeCycles?.random() ?: 1
        set(value) {
            if (value < 0) {
                throw IllegalArgumentException("You cannot set the life cycles to less than 0")
            }
            if (field != value) {
                this.markDirty()
            }
            field = value
        }

    private val growthPoints = arrayListOf<Identifier>()

    /**
     * Returns the [BerryBlock] behind this entity.
     *
     * @return The [BerryBlock].
     */
    fun berryBlock() = this.cachedState.block as BerryBlock

    /**
     * Returns the [Berry] behind this entity,
     * This will be null if it doesn't exist in the [Berries] registry.
     *
     * @return The [Berry] if existing.
     */
    fun berry() = this.berryBlock().berry()

    /**
     * Generates a random amount of growth points for this tree.
     *
     * @param world The [World] the tree is in.
     * @param state The [BlockState] of the tree.
     * @param pos The [BlockPos] of the tree.
     * @param placer The [LivingEntity] that placed the tree if any.
     */
    fun generateGrowthPoints(world: World, state: BlockState, pos: BlockPos, placer: LivingEntity?) {
        val berry = this.berry() ?: return
        val yield = berry.calculateYield(world, state, pos, placer)
        this.growthPoints.clear()
        repeat(yield) {
            this.growthPoints += berry.identifier
        }
        this.markDirty()
    }

    /**
     * Calculates the berry produce in [ItemStack] form.
     *
     * @param world The [World] the tree is in.
     * @param state The [BlockState] of the tree.
     * @param pos The [BlockPos] of the tree.
     * @param player The [PlayerEntity] harvesting the tree.
     * @return The resulting [ItemStack]s to be dropped.
     */
    fun harvest(world: World, state: BlockState, pos: BlockPos, player: PlayerEntity): Collection<ItemStack> {
        val drops = arrayListOf<ItemStack>()
        if (this.lifeCycles <= 0) {
            this.consumeLife(world, pos, state, player)
            return drops
        }
        val unique = this.growthPoints.groupingBy { it }.eachCount()
        unique.forEach { (identifier, amount) ->
            val berryItem = Berries.getByIdentifier(identifier)?.item()
            if (berryItem != null) {
                var remain = amount
                while (remain > 0) {
                    val count = remain.coerceAtMost(berryItem.maxCount)
                    drops += ItemStack(berryItem, count)
                    remain -= count
                }
            }
        }
        this.berry()?.let { berry ->
            if (player is ServerPlayerEntity) {
                CobblemonEvents.BERRY_HARVEST.post(BerryHarvestEvent(berry, player, world, pos, state, this, drops))
            }
        }
        this.consumeLife(world, pos, state, player)
        return drops
    }

    /**
     * TODO
     *
     * @param isFlower
     * @return
     */
    internal fun berryAndShape(isFlower: Boolean): Collection<Pair<Berry, VoxelShape>> {
        val shapes = arrayListOf<Pair<Berry, VoxelShape>>()
        for (index in this.growthPoints.indices) {
            val identifier = this.growthPoints[index]
            val berry = Berries.getByIdentifier(identifier) ?: continue
            val shape = berry.shapeAt(index, isFlower)
            shapes.add(berry to shape)
        }
        return shapes
    }

    override fun readNbt(nbt: NbtCompound) {
        this.growthPoints.clear()
        this.lifeCycles = nbt.getInt(LIFE_CYCLES).coerceAtLeast(0)
        nbt.getList(GROWTH_POINTS, NbtList.STRING_TYPE.toInt()).filterIsInstance<NbtString>().forEach { element ->
            // In case some 3rd party mutates the NBT incorrectly
            try {
                this.growthPoints += Identifier(element.asString())
            } catch (ignored: InvalidIdentifierException) {}
        }
    }

    override fun writeNbt(nbt: NbtCompound) {
        nbt.putInt(LIFE_CYCLES, this.lifeCycles)
        val list = NbtList()
        list += this.growthPoints.map { NbtString.of(it.toString()) }
        nbt.put(GROWTH_POINTS, list)
    }

    private fun consumeLife(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity) {
        if (this.lifeCycles > 1) {
            this.lifeCycles--
            world.setBlockState(pos, state.with(BerryBlock.AGE, 0), 2)
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, state))
            this.generateGrowthPoints(world, state, pos, player)
            return
        }
        world.breakBlock(pos, false)
    }

    companion object {

        private const val LIFE_CYCLES = "life_cycles"
        private const val GROWTH_POINTS = "growth_points"

    }

}