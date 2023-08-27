/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.berry.Berries
import com.cobblemon.mod.common.api.berry.Berry
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.events.berry.BerryMutationOfferEvent
import com.cobblemon.mod.common.api.events.berry.BerryMutationResultEvent
import com.cobblemon.mod.common.api.mulch.MulchVariant
import com.cobblemon.mod.common.api.mulch.Mulchable
import com.cobblemon.mod.common.api.tags.CobblemonBlockTags
import com.cobblemon.mod.common.block.entity.BerryBlockEntity
import com.cobblemon.mod.common.block.entity.HealingMachineBlockEntity
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.ShovelItem
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.EnumProperty
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

class BerryBlock(private val berryIdentifier: Identifier, settings: Settings) : BlockWithEntity(settings), Fertilizable, Mulchable {

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

    override fun <T : BlockEntity> getTicker(world: World, blockState: BlockState, blockWithEntityType: BlockEntityType<T>): BlockEntityTicker<T>? = checkType(blockWithEntityType, CobblemonBlockEntities.BERRY, BerryBlockEntity.TICKER)

    init {
        defaultState = this.stateManager.defaultState
            .with(HAS_MULCH, false)
            .with(WAS_GENERATED, false)
            .with(AGE, 0)
    }

    override fun grow(world: ServerWorld, random: Random, pos: BlockPos, state: BlockState) {
        val curAge = state.get(AGE)
        val newAge = curAge + 1
        if (newAge > FRUIT_AGE) return
        var newState = state.with(AGE, newAge)
        if (curAge == MATURE_AGE) {
            determineMutation(world, random, pos, state)
            if (state.get(MULCH_TYPE) == MulchVariant.SURPRISE) {
                val duration = state.get(MULCH_DURATION)
                if (duration > 0) {
                    newState = newState.with(MULCH_DURATION, duration - 1)
                }
            }
        }
        world.setBlockState(pos, newState, Block.NOTIFY_LISTENERS)
        val entity = world.getBlockEntity(pos)
        (entity as BerryBlockEntity).goToNextStageTimer(FRUIT_AGE - curAge)
    }

    fun determineMutation(world: World, random: Random, pos: BlockPos, state: BlockState) {
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
                    var mutateChance = 125
                    if (state.get(MULCH_TYPE) == MulchVariant.SURPRISE && state.get(MULCH_DURATION) > 0) {
                        mutateChance *= 4
                    }
                    val mutation = if (random.nextInt(1000) < mutateChance) mutations.random() else null
                    (world.getBlockEntity(pos) as? BerryBlockEntity)?.let { blockEntity ->
                        CobblemonEvents.BERRY_MUTATION_RESULT.post(BerryMutationResultEvent(berry, world, state, pos, berryMutationOffer.mutations, mutation)) { berryMutationResult ->
                            berryMutationResult.pickedMutation?.let { mutation -> blockEntity.mutate(mutation) }
                        }
                    }
                }
            }
        }
    }

    override fun canHaveMulchApplied(
        world: ServerWorld,
        pos: BlockPos,
        state: BlockState,
        variant: MulchVariant
    ): Boolean {
        return !state.get(HAS_MULCH) || !state.get(MULCH_TYPE).equals(variant)
    }

    override fun applyMulch(
        world: ServerWorld,
        random: Random,
        pos: BlockPos,
        state: BlockState,
        variant: MulchVariant
    ) {
        var newState = state.with(MULCH_TYPE, variant).with(HAS_MULCH, true)
        world.setBlockState(pos, state.with(HAS_MULCH, true), Block.NOTIFY_LISTENERS)
        if (!variant.isBiomeMulch) {
            newState = newState.with(MULCH_DURATION, variant.duration)
        }
        world.setBlockState(pos, newState, Block.NOTIFY_LISTENERS)
        world.playSound(null, pos, CobblemonSounds.MULCH_PLACE, SoundCategory.BLOCKS)
    }

    @Deprecated("Deprecated in Java")
    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        if(player.getStackInHand(hand).item is ShovelItem && state.get(HAS_MULCH)) {
            world.setBlockState(pos, state.with(HAS_MULCH, false), Block.NOTIFY_LISTENERS)
            world.playSound(null, pos, CobblemonSounds.MULCH_REMOVE, SoundCategory.BLOCKS)
            return ActionResult.SUCCESS
        }

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
        return state.get(WAS_GENERATED) || world.getBlockState(pos.down()).isIn(CobblemonBlockTags.BERRY_SOIL)
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
        builder.add(HAS_MULCH)
        builder.add(MULCH_TYPE)
        builder.add(MULCH_DURATION)
        builder.add(WAS_GENERATED)
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
        val HAS_MULCH: BooleanProperty = BooleanProperty.of("has_mulch")
        val WAS_GENERATED: BooleanProperty = BooleanProperty.of("generated")
        val MULCH_TYPE: EnumProperty<MulchVariant> = EnumProperty.of("mulch_type", MulchVariant::class.java)
        val MULCH_DURATION: IntProperty = IntProperty.of("mulch_duration", 0, 3)
    }
}
