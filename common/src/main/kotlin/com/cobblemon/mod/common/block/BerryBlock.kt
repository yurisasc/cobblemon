/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block

import com.cobblemon.mod.common.api.berry.Berries
import com.cobblemon.mod.common.api.berry.Berry
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.events.berry.BerryMutationOfferEvent
import com.cobblemon.mod.common.api.events.berry.BerryMutationResultEvent
import com.cobblemon.mod.common.api.tags.CobblemonBlockTags
import com.cobblemon.mod.common.block.entity.BerryBlockEntity
import net.minecraft.block.*
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.IntProperty
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldAccess
import net.minecraft.world.WorldView

class BerryBlock(private val berryIdentifier: Identifier, settings: Settings) : BlockWithEntity(settings), Fertilizable {

    private val lookupDirections = setOf(Direction.NORTH, Direction.EAST, Direction.WEST, Direction.SOUTH)

    /**
     * Returns the [Berry] behind this block,
     * This will be null if it doesn't exist in the [Berries] registry.
     *
     * @return The [Berry] if existing.
     */
    fun berry(): Berry? = Berries.getByIdentifier(this.berryIdentifier)

    override fun createBlockEntity(pos: BlockPos, state: BlockState) = BerryBlockEntity(pos, state, berryIdentifier)

    override fun isFertilizable(world: WorldView, pos: BlockPos, state: BlockState, isClient: Boolean) = !this.isMaxAge(state)

    override fun canGrow(world: World, random: Random, pos: BlockPos, state: BlockState) = !this.isMaxAge(state)

    override fun hasRandomTicks(state: BlockState) = !this.isMaxAge(state)

    /*
    @Deprecated("Deprecated in Java")
    override fun randomTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        val lowerLimit = this.berry()?.growthTime!!.first
        if (world.random.nextInt(5) == 0 && this.canGrow(world, random, pos, state)) {
            this.grow(world, random, pos, state)
        }
    }
    */

    //override fun tick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {

    //}

    override fun grow(world: ServerWorld, random: Random, pos: BlockPos, state: BlockState) {
        val newAge = state.get(AGE) + 1
        if (newAge == MATURE_AGE) {
            val mutations = hashSetOf<Berry>()
            for (direction in this.lookupDirections) {
                val redirectedPos = pos.add(direction.vector)
                val redirectedState = world.getBlockState(redirectedPos)
                val berryBlock = redirectedState.block as? BerryBlock ?: continue
                val berry = berryBlock.berry() ?: continue
                val mutation = this.berry()?.mutationWith(berry) ?: continue
                mutations += mutation
            }
            this.berry()?.let { berry ->
                CobblemonEvents.BERRY_MUTATION_OFFER.post(BerryMutationOfferEvent(berry, world, state, pos, mutations)) { berryMutationOffer ->
                    if (berryMutationOffer.mutations.isNotEmpty()) {
                        (world.getBlockEntity(pos) as? BerryBlockEntity)?.let { blockEntity ->
                            CobblemonEvents.BERRY_MUTATION_RESULT.post(BerryMutationResultEvent(berry, world, state, pos, berryMutationOffer.mutations, berryMutationOffer.mutations.random())) { berryMutationResult ->
                                berryMutationResult.pickedMutation?.let { mutation -> blockEntity.mutate(mutation) }
                            }
                        }
                    }
                }
            }
        }
        world.setBlockState(pos, state.with(AGE, newAge), Block.NOTIFY_LISTENERS)
    }

    @Deprecated("Deprecated in Java")
    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        if (player.getStackInHand(hand).isOf(Items.BONE_MEAL)) {
            return ActionResult.PASS
        }
        else if (this.isMaxAge(state)) {
            val blockEntity = world.getBlockEntity(pos) as? BerryBlockEntity ?: return ActionResult.PASS
            blockEntity.harvest(world, state, pos, player).forEach { drop ->
                Block.dropStack(world, pos, drop)
            }
            return ActionResult.success(world.isClient)
        }
        return super.onUse(state, world, pos, player, hand, hit)
    }

    @Deprecated("Deprecated in Java")
    override fun canPlaceAt(state: BlockState, world: WorldView, pos: BlockPos): Boolean {
        return world.getBlockState(pos.down()).isIn(CobblemonBlockTags.BERRY_SOIL)
    }

    @Deprecated("Deprecated in Java")
    override fun getStateForNeighborUpdate(state: BlockState, direction: Direction, neighborState: BlockState, world: WorldAccess, pos: BlockPos, neighborPos: BlockPos): BlockState {
        return if (state.canPlaceAt(world, pos)) super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos) else Blocks.AIR.defaultState
    }

    override fun onPlaced(world: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, itemStack: ItemStack) {
        if (!world.isClient) {
            val blockEntity = world.getBlockEntity(pos) as? BerryBlockEntity ?: return
            blockEntity.generateGrowthPoints(world, state, pos, placer)
        }
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(AGE)
    }

    override fun getPickStack(world: BlockView?, pos: BlockPos?, state: BlockState?): ItemStack {
        val berryItem = this.berry()?.item() ?: return ItemStack.EMPTY
        return ItemStack(berryItem)
    }

    @Deprecated("Deprecated in Java")
    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        val berry = this.berry() ?: return VoxelShapes.fullCube()
        return if (state.get(AGE) >= MATURE_AGE) berry.matureShape else berry.sproutShape
    }

    private fun isMaxAge(state: BlockState) = state.get(AGE) == FRUIT_AGE

    @Deprecated("Deprecated in Java")
    override fun getRenderType(blockState: BlockState) = BlockRenderType.MODEL

    companion object {

        const val MATURE_AGE = 3
        const val FLOWER_AGE = 4
        const val FRUIT_AGE = 5
        val AGE: IntProperty = IntProperty.of("age", 0, FRUIT_AGE)

    }
}