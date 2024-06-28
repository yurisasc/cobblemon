/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block

import com.cobblemon.mod.common.CobblemonBlockEntities
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
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
//import dev.lambdaurora.lambdynlights.util.SodiumDynamicLightHandler.pos
import net.minecraft.world.level.block.Block
import net.minecraft.block.Blocks
import net.minecraft.block.Fertilizable
import net.minecraft.block.ShapeContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.ShovelItem
import net.minecraft.server.level.ServerLevel
import net.minecraft.sound.SoundCategory
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.EnumProperty
import net.minecraft.state.property.IntProperty
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.core.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.level.Level
import net.minecraft.world.WorldAccess
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.state.BlockState

class BerryBlock(private val berryIdentifier: ResourceLocation, settings: Settings) : BaseEntityBlock(settings), Fertilizable, Mulchable {

    private val lookupDirections = setOf(Direction.NORTH, Direction.EAST, Direction.WEST, Direction.SOUTH)

    /**
     * Returns the [Berry] behind this block,
     * This will be null if it doesn't exist in the [Berries] registry.
     *
     * @return The [Berry] if existing.
     */
    fun berry(): Berry? = Berries.getByIdentifier(this.berryIdentifier)

    override fun onBreak(world: Level, pos: BlockPos, state: BlockState, player: Player): BlockState {
        if (!player.isCreative && state.get(AGE) == FRUIT_AGE) {
            val treeEntity = world.getBlockEntity(pos) as BerryBlockEntity
            treeEntity.harvest(world, state, pos, player).forEach { drop -> Block.dropStack(world, pos, drop) }
        }
        return super.onBreak(world, pos, state, player)
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState) = BerryBlockEntity(pos, state, berryIdentifier)

    override fun isFertilizable(world: LevelReader, pos: BlockPos, state: BlockState) = !this.isMaxAge(state)

    override fun canGrow(world: Level, random: Random, pos: BlockPos, state: BlockState) = !this.isMaxAge(state)

    override fun <T : BlockEntity> getTicker(world: Level, blockState: BlockState, blockWithEntityType: BlockEntityType<T>): BlockEntityTicker<T>? = validateTicker(blockWithEntityType, CobblemonBlockEntities.BERRY, BerryBlockEntity.TICKER)

    init {
        defaultState = this.stateManager.defaultState
            .with(WAS_GENERATED, false)
            .with(MULCH, MulchVariant.NONE)
            .with(AGE, 0)
            .with(IS_ROOTED, false)
    }

    override fun grow(world: ServerLevel, random: Random, pos: BlockPos, state: BlockState) {
        growHelper(world, random, pos, state, true)
    }

    //grow, but cooler
    fun growHelper(world: ServerLevel, random: Random, pos: BlockPos, state: BlockState, boneMealed: Boolean = false) {
        val berry = berry() ?: return
        if (boneMealed && random.nextFloat() > berry.boneMealChance) return
        val curAge = state.get(AGE)
        val newAge = curAge + 1
        if (newAge > FRUIT_AGE) return
        val newState = state.with(AGE, newAge)
        val treeEntity = world.getBlockEntity(pos) as BerryBlockEntity
        if (curAge == MATURE_AGE) {
            treeEntity.generateGrowthPoints(world, newState, pos, null)
            determineMutation(world, random, pos, newState)
        }

        world.setBlockState(pos, newState, Block.NOTIFY_LISTENERS)
        convertMulchToEntity(world, newState, pos)
        treeEntity.goToNextStageTimer(FRUIT_AGE - curAge)
        treeEntity.markDirty()
    }

    fun determineMutation(world: Level, random: Random, pos: BlockPos, state: BlockState) {
        val mutations = hashSetOf<Berry>()
        val treeEntity = world.getBlockEntity(pos) as BerryBlockEntity
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
                    if (getMulch(treeEntity) == MulchVariant.SURPRISE) {
                        mutateChance *= 4
                        treeEntity.decrementMulchDuration(world, pos, state)
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
        world: ServerLevel,
        pos: BlockPos,
        state: BlockState,
        variant: MulchVariant
    ): Boolean {
        val underBlockState = world.getBlockState(pos.down())
        val validSoil = state.get(IS_ROOTED) || underBlockState.isIn(CobblemonBlockTags.BERRY_SOIL)
        val treeEntity = world.getBlockEntity(pos) as? BerryBlockEntity ?: return false
        return getMulch(treeEntity) == MulchVariant.NONE && state.get(AGE) < FLOWER_AGE && validSoil
    }

    override fun applyMulch(
        world: ServerLevel,
        random: Random,
        pos: BlockPos,
        state: BlockState,
        variant: MulchVariant
    ) {
        val treeEntity = world.getBlockEntity(pos) as BerryBlockEntity
        treeEntity.setMulch(variant, world, state, pos)
        world.playSound(null, pos, CobblemonSounds.MULCH_PLACE, SoundCategory.BLOCKS, 0.6F, 1F)
    }

    @Deprecated("Deprecated in Java")
    override fun onUse(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        player: Player,
        hit: BlockHitResult
    ): ActionResult? {
        val treeEntity = world.getBlockEntity(pos) as BerryBlockEntity
        if (player.getStackInHand(Hand.MAIN_HAND).item is ShovelItem && getMulch(treeEntity) != MulchVariant.NONE) {
            treeEntity.markDirty()
            world.playSound(null, pos, CobblemonSounds.MULCH_REMOVE, SoundCategory.BLOCKS, 0.6F, 1F)
            this.spawnBreakParticles(world, player, pos, state.with(AGE, 0))
            return ActionResult.SUCCESS
        }

        if (player.getStackInHand(Hand.MAIN_HAND).isOf(Items.BONE_MEAL) && !this.isMaxAge(state)) {
            return ActionResult.PASS
        } else if (this.isMaxAge(state)) {
            val blockEntity = world.getBlockEntity(pos) as? BerryBlockEntity ?: return ActionResult.PASS
            blockEntity.harvest(world, state, pos, player).forEach { drop ->
                Block.dropStack(world, pos, drop)
            }
            world.playSound(null, pos, CobblemonSounds.BERRY_HARVEST, SoundCategory.BLOCKS, 0.4F, 1F)
            return ActionResult.success(world.isClient)
        }
        return super.onUse(state, world, pos, player, hit)
    }

    @Deprecated("Deprecated in Java")
    override fun canPlaceAt(state: BlockState, world: LevelReader, pos: BlockPos): Boolean {
        val below = world.getBlockState(pos.down())
        return (state.get(WAS_GENERATED) && below.isIn(CobblemonBlockTags.BERRY_WILD_SOIL))
                || below.isIn(CobblemonBlockTags.BERRY_SOIL)
                || state.get(IS_ROOTED)
    }

    override fun getCodec(): MapCodec<out BlockWithEntity> {
        return CODEC
    }

    @Deprecated("Deprecated in Java")
    override fun getStateForNeighborUpdate(state: BlockState, direction: Direction, neighborState: BlockState, world: WorldAccess, pos: BlockPos, neighborPos: BlockPos): BlockState {
        return if (state.canPlaceAt(world, pos)) super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos) else Blocks.AIR.defaultState
    }

    override fun onPlaced(world: Level, pos: BlockPos, state: BlockState, placer: LivingEntity?, itemStack: ItemStack) {
//        if (!world.isClient) {
//            val blockEntity = world.getBlockEntity(pos) as? BerryBlockEntity ?: return
//            blockEntity.generateGrowthPoints(world, state, pos, placer)
//        }
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(AGE)
        builder.add(WAS_GENERATED)
        builder.add(MULCH)
        builder.add(IS_ROOTED)
    }

    override fun getCloneItemStack(world: LevelReader?, pos: BlockPos?, state: BlockState?): ItemStack {
        val berryItem = this.berry()?.item() ?: return ItemStack.EMPTY
        return ItemStack(berryItem)
    }

    @Deprecated("Deprecated in Java")
    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        val berry = this.berry() ?: return VoxelShapes.fullCube()
        return when (state.get(AGE)) {
            0 -> PLANTED_SHAPE
            1 -> PLANTED_SHAPE
            2 -> berry.sproutShape
            else -> berry.matureShape
        }
    }

    private fun isMaxAge(state: BlockState) = state.get(AGE) == FRUIT_AGE

    @Deprecated("Deprecated in Java")
    override fun getRenderShape(blockState: BlockState) = RenderShape.MODEL

    companion object {
        val CODEC: MapCodec<BerryBlock> = RecordCodecBuilder.mapCodec { it.group(
            ResourceLocation.CODEC.fieldOf("berry").forGetter(BerryBlock::berryIdentifier),
            createSettingsCodec()
        ).apply(it, ::BerryBlock) }

        const val MATURE_AGE = 3
        const val FLOWER_AGE = 4
        const val FRUIT_AGE = 5

        val AGE: IntProperty = IntProperty.of("age", 0, FRUIT_AGE)
        val MULCH: EnumProperty<MulchVariant> = EnumProperty.of("mulch", MulchVariant::class.java)
        val WAS_GENERATED: BooleanProperty = BooleanProperty.of("generated")
        val IS_ROOTED: BooleanProperty = BooleanProperty.of("rooted")
//        val PLANTED_SHAPE = VoxelShapes.union(
//            VoxelShapes.cuboid(0.3125, -0.0625, 0.3125, 0.6875, 0.0, 0.6875),
//            VoxelShapes.cuboid(0.375, 0.0, 0.375, 0.625, 0.0625, 0.625)
//        )
        val PLANTED_SHAPE = VoxelShapes.cuboid(0.0, -0.1, 0.0, 1.0, 0.25, 1.0)


        val STANDARD_SPROUT = listOf(Box(0.0, -1.0, 0.0, 16.0, 16.0, 16.0))
        val STANDARD_MATURE = listOf(Box(0.0, -1.0, 0.0, 16.0, 24.0, 16.0))

        val SHORT_SPROUT = listOf(Box(0.0, -1.0, 0.0, 16.0, 12.0, 16.0))
        val SHORT_MATURE = listOf(Box(0.0, -1.0, 0.0, 16.0, 16.0, 16.0))

        val VOLCANO_SPROUT = listOf(Box(0.0, -1.0, 0.0, 16.0, 6.0, 16.0))
        val VOLCANO_MATURE = listOf(Box(0.0, -1.0, 0.0, 16.0, 16.0, 16.0))

        val NEST_SPROUT = listOf(Box(0.0, -1.0, 0.0, 16.0, 6.0, 16.0))
        val NEST_MATURE = listOf(Box(0.0, -1.0, 0.0, 16.0, 20.0, 16.0))

        val FRILL_SPROUT = listOf(Box(0.0, -1.0, 0.0, 16.0, 8.0, 16.0))
        val FRILL_MATURE = listOf(Box(0.0, -1.0, 0.0, 16.0, 14.0, 16.0))

        val BLOCK_SPROUT = listOf(Box(0.0, -1.0, 0.0, 16.0, 17.0, 16.0))
        val BLOCK_MATURE = listOf(Box(0.0, -1.0, 0.0, 16.0, 24.0, 16.0))

        val PYRAMID_SPROUT = listOf(Box(0.0, -1.0, 0.0, 16.0, 17.0, 16.0))
        val PYRAMID_MATURE = listOf(Box(0.0, -1.0, 0.0, 16.0, 24.0, 16.0))

        val TAIL_SPROUT = listOf(Box(0.0, -1.0, 0.0, 16.0, 16.0, 16.0))
        val TAIL_MATURE = listOf(Box(0.0, 1.0, 0.0, 16.0, 24.0, 16.0))

        val SWORD_SPROUT = listOf(Box(0.0, -1.0, 0.0, 16.0, 5.0, 16.0))
        val SWORD_MATURE = listOf(Box(0.0, -1.0, 0.0, 16.0, 24.0, 16.0))

        val PLATFORM_SPROUT = listOf(Box(0.0, -1.0, 0.0, 16.0, 7.0, 16.0))
        val PLATFORM_MATURE = listOf(Box(0.0, -1.0, 0.0, 16.0, 22.0, 16.0))

        val STAND_SPROUT = listOf(Box(0.0, -1.0, 0.0, 16.0, 13.0, 16.0))
        val STAND_MATURE = listOf(Box(0.0, -1.0, 0.0, 16.0, 24.0, 16.0))

        val CONE_SPROUT = listOf(Box(0.0, -1.0, 0.0, 16.0, 16.0, 16.0))
        val CONE_MATURE = listOf(Box(0.0, -1.0, 0.0, 16.0, 23.0, 16.0))

        val SQUAT_SPROUT = listOf(Box(0.0, -1.0, 0.0, 16.0, 12.0, 16.0))
        val SQUAT_MATURE = listOf(Box(0.0, -1.0, 0.0, 16.0, 22.0, 16.0))

        val LANTERN_SPROUT = listOf(Box(0.0, -1.0, 0.0, 16.0, 20.0, 16.0))
        val LANTERN_MATURE = listOf(Box(0.0, -1.0, 0.0, 16.0, 24.0, 16.0))

        val BOX_SPROUT = listOf(Box(0.0, -1.0, 0.0, 16.0, 16.0, 16.0))
        val BOX_MATURE = listOf(Box(0.0, -1.0, 0.0, 16.0, 24.0, 16.0))

        val BLOSSOM_SPROUT = listOf(Box(0.0, -1.0, 0.0, 16.0, 4.0, 16.0))
        val BLOSSOM_MATURE = listOf(Box(0.0, -1.0, 0.0, 16.0, 6.0, 16.0))

        val LILYPAD_SPROUT = listOf(Box(0.0, -1.0, 0.0, 16.0, 11.0, 16.0))
        val LILYPAD_MATURE = listOf(Box(0.0, -1.0, 0.0, 16.0, 16.0, 16.0))

        val TALL_SPROUT = listOf(Box(0.0, -1.0, 0.0, 16.0, 16.0, 16.0))
        val TALL_MATURE = listOf(Box(0.0, -1.0, 0.0, 16.0, 24.0, 16.0))


        fun getMulch(entity: BerryBlockEntity) = entity.mulchVariant

        fun convertMulchToEntity(world: ServerLevel, state: BlockState, pos: BlockPos) {
            val entity = world.getBlockEntity(pos) as? BerryBlockEntity ?: return
            if (state.get(MULCH) != MulchVariant.NONE && state.get(MULCH) != entity.mulchVariant) {
                entity.mulchVariant = state.get(MULCH)
                world.setBlockState(pos, state.with(MULCH, MulchVariant.NONE))
            }
        }
    }
}