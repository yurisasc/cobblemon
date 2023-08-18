/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block.entity

import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.api.berry.Berries
import com.cobblemon.mod.common.api.berry.Berry
import com.cobblemon.mod.common.api.berry.GrowthPoint
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.events.berry.BerryHarvestEvent
import com.cobblemon.mod.common.api.mulch.MulchVariant
import com.cobblemon.mod.common.block.BerryBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtString
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.util.InvalidIdentifierException
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent

class BerryBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(CobblemonBlockEntities.BERRY, pos, state) {
    lateinit var berryIdentifier: Identifier

    constructor(pos: BlockPos, state: BlockState, berryIdentifier: Identifier): this(pos, state) {
        this.berryIdentifier = berryIdentifier
        resetGrowTimers(pos, state)
        if (state.get(BerryBlock.WAS_GENERATED) && state.get(BerryBlock.AGE) >= 4) {
            generateSimpleYields()
        }
    }

    fun resetGrowTimers(pos: BlockPos, state: BlockState) {
        val curAge = state.get(BerryBlock.AGE)
        if (curAge != 5) {
            val berry = Berries.getByIdentifier(berryIdentifier)!!
            var multiplier = 10
            if (state.get(BerryBlock.HAS_MULCH) and (state[BerryBlock.MULCH_TYPE] == MulchVariant.GROWTH)) {
                val duration = state.get(BerryBlock.MULCH_DURATION)
                world?.setBlockState(pos, state.with(BerryBlock.MULCH_DURATION, duration - 1))
                multiplier = 15
            }
            val upperGrowthLimit = ((if (curAge == 0) berry.growthTime.last else berry.refreshRate.last) * multiplier) / 10
            val lowerGrowthLimit = ((if (curAge == 0) berry.growthTime.first else berry.refreshRate.first) * multiplier) /10
            val growthRange = lowerGrowthLimit..upperGrowthLimit
            growthTimer = growthRange.random() * ticksPerMinute

            goToNextStageTimer(BerryBlock.FRUIT_AGE - curAge)
        }
    }

    fun goToNextStageTimer(stagesLeft: Int) {
        val avgStageTime = growthTimer / stagesLeft
        stageTimer = this.world?.random?.nextBetween((avgStageTime * 8) / 10, avgStageTime) ?:
                (((Math.random() *  0.2) + 0.8) * avgStageTime).toInt()
        growthTimer -= stageTimer
    }

    private val ticksPerMinute = 1200


    //The time left for the tree to grow to stage 5
    var growthTimer: Int = 72000
        set(value) {
            if (value < 0) {
                throw IllegalArgumentException("You cannot set the growth time to less than zero")
            }
            if (field != value) {
                this.markDirty()
            }
            field = value
        }

    //The time left for the tree to grow to the next stage
    var stageTimer: Int = growthTimer / BerryBlock.MATURE_AGE
        set(value) {
            if (value < 0) {
                throw IllegalArgumentException("You cannot set the stage growth time to less than zero")
            }
            if (field != value) {
                this.markDirty()
            }
            field = value
        }


    private val growthPoints = arrayListOf<Identifier>()

    // Just a cheat to not invoke markDirty unnecessarily
    private var wasLoading = false

    /**
     * Returns the [Berry] behind this entity,
     * This will be null if it doesn't exist in the [Berries] registry.
     *
     * @return The [Berry] if existing.
     */
    fun berry() = Berries.getByIdentifier(berryIdentifier)

    /**
     * Generates a random amount of growth points for this tree.
     *
     * @param world The [World] the tree is in.
     * @param state The [BlockState] of the tree.
     * @param pos The [BlockPos] of the tree.
     * @param placer The [LivingEntity] tending to the tree if any.
     */
    fun generateGrowthPoints(world: World, state: BlockState, pos: BlockPos, placer: LivingEntity?) {
        val berry = this.berry() ?: return
        val yield = berry.calculateYield(world, state, pos, placer)
        val hasRichMulch = state.get(BerryBlock.MULCH_TYPE).equals(MulchVariant.RICH)
        var newState = state
        //Originally I decremented duration when harvesting, but that will cause weird interactions
        //Like if you mulch a fully grown tree, harvesting it decrements the mulch duration even
        //though the mulch had no effect. So now we decrement duration on the mulch effect
        if (hasRichMulch) {
            val duration = state.get(BerryBlock.MULCH_DURATION)
            if (duration > 0) {
                newState = state.with(BerryBlock.MULCH_DURATION, duration - 1)
            }
        }
        this.growthPoints.clear()
        repeat(yield) {
            this.growthPoints += berry.identifier
        }
        world.setBlockState(pos, newState, Block.NOTIFY_LISTENERS)
        this.markDirty()
    }

    //Used for naturally spawning berries
    fun generateSimpleYields() {
        val numBerries = berry()?.baseYield?.random() ?: return
        repeat(numBerries) {
            growthPoints.add(berryIdentifier)
        }
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
        //if (this.lifeCycles <= 0) {
        //    this.consumeLife(world, pos, state, player)
        //    return drops
        //}
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
        val hasBiomeMulch = state.get(BerryBlock.MULCH_TYPE).isBiomeMulch
        var newState = state
        if (!hasBiomeMulch && state.get(BerryBlock.HAS_MULCH)) {
            val curDuration = state.get(BerryBlock.MULCH_DURATION)
            if (curDuration == 0)  {
                newState = newState.with(BerryBlock.HAS_MULCH, false)
            }
            world.setBlockState(pos, newState, Block.NOTIFY_LISTENERS)
        }
        //Since refresh overwrites any state changes we do in this method, pass newState
        refresh(world, pos, newState, player)
        //this.consumeLife(world, pos, state, player)
        return drops
    }

    override fun readNbt(nbt: NbtCompound) {
        this.berryIdentifier = Identifier(nbt.getString(BERRY).takeIf { it.isNotBlank() } ?: "cobblemon:pecha")
        this.wasLoading = true
        this.growthPoints.clear()
        this.growthTimer = nbt.getInt(GROWTH_TIMER).coerceAtLeast(0)
        this.stageTimer = nbt.getInt(STAGE_TIMER).coerceAtLeast(0)
        //this.lifeCycles = nbt.getInt(LIFE_CYCLES).coerceAtLeast(0)
        nbt.getList(GROWTH_POINTS, NbtList.STRING_TYPE.toInt()).filterIsInstance<NbtString>().forEach { element ->
            // In case some 3rd party mutates the NBT incorrectly
            try {
                val identifier = Identifier(element.asString())
                this.growthPoints += identifier
            } catch (ignored: InvalidIdentifierException) {}
        }
        this.wasLoading = false
    }

    override fun writeNbt(nbt: NbtCompound) {
        //nbt.putInt(LIFE_CYCLES, this.lifeCycles)
        nbt.putInt(GROWTH_TIMER, this.growthTimer)
        nbt.putInt(STAGE_TIMER, this.stageTimer)
        val list = NbtList()
        list += this.growthPoints.map { NbtString.of(it.toString()) }
        nbt.put(GROWTH_POINTS, list)
        nbt.putString(BERRY, berryIdentifier.toString())
    }

    override fun markDirty() {
        if (!this.wasLoading) {
            super.markDirty()
        }
    }

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener>? {
        return BlockEntityUpdateS2CPacket.create(this)
    }

    override fun toInitialChunkDataNbt(): NbtCompound {
        return this.createNbt()
    }

    /**
     * Collects each [Berry] and [GrowthPoint].
     *
     * @return Collection of the [Berry] and [GrowthPoint].
     */
    internal fun berryAndGrowthPoint(): Collection<Pair<Berry, GrowthPoint>> {
        val baseBerry = this.berry() ?: return emptyList()
        val berryPoints = arrayListOf<Pair<Berry, GrowthPoint>>()
        for ((index, identifier) in this.growthPoints.withIndex()) {
            val berry = Berries.getByIdentifier(identifier) ?: continue
            berryPoints.add(berry to baseBerry.growthPoints[index])
        }
        return berryPoints
    }

    /**
     * Inserts the given [berry] as a mutation in a random [growthPoints].
     *
     * @param berry The [Berry] being mutated.
     */
    internal fun mutate(berry: Berry) {
        if (this.growthPoints.isEmpty()) {
            return
        }
        val index = this.growthPoints.indices.random()
        this.growthPoints[index] = berry.identifier
        this.markDirty()
    }

    private fun refresh(world: World, pos: BlockPos, state:BlockState, player: PlayerEntity) {
        val newState = state.with(BerryBlock.AGE, 3)
        world.setBlockState(pos, newState, Block.NOTIFY_LISTENERS)
        world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos)
        this.generateGrowthPoints(world, newState, pos, player)
        resetGrowTimers(pos, newState)
        return
    }

    /*
    private fun consumeLife(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity) {
        if (this.lifeCycles > 1) {
            this.lifeCycles--
            world.setBlockState(pos, state.with(BerryBlock.AGE, 0), Block.NOTIFY_LISTENERS)
            world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos)
            this.generateGrowthPoints(world, state, pos, player)
            return
        }
        world.breakBlock(pos, false)
    }
    */

    companion object {
        internal val TICKER = BlockEntityTicker<BerryBlockEntity> { world, pos, state, blockEntity ->
            if (world.isClient) return@BlockEntityTicker
            if (blockEntity.stageTimer > 0) {
                blockEntity.stageTimer--
            }
            if (blockEntity.stageTimer == 0) {
                (state.block as BerryBlock).grow(world as ServerWorld, world.random, pos, state)
            }

        }
        //private const val LIFE_CYCLES = "life_cycles"
        private const val GROWTH_POINTS = "growth_points"
        private const val GROWTH_TIMER = "growth_timer"
        private const val STAGE_TIMER = "stage_timer"
        private const val BERRY = "berry"
    }

}
