package com.cobblemon.mod.common.block.chest

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.block.entity.GildedChestBlockEntity
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.block.*
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.sound.SoundCategory
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.ItemScatterer
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import java.util.*

class GildedChestBlock(settings: Settings, val fake: Boolean = false) : BlockWithEntity(settings) {

    init {
        defaultState = defaultState.with(Properties.HORIZONTAL_FACING, Direction.NORTH)
    }

    companion object {
        val POKEMON_ARGS = "gimmighoul"
        val LEVEL_RANGE = 5..30

        val NORTH_OUTLINE = VoxelShapes.union(
            VoxelShapes.cuboid(0.0, 0.0, 0.25, 1.0, 1.0, 0.9375)
        )
        val SOUTH_OUTLINE = VoxelShapes.union(
            VoxelShapes.cuboid(0.0, 0.0, 0.0625, 1.0, 1.0, 0.75)
        )
        val EAST_OUTLINE = VoxelShapes.union(
            VoxelShapes.cuboid(0.0625, 0.0, 0.0, 0.75, 1.0, 1.0)
        )
        val WEST_OUTLINE = VoxelShapes.union(
            VoxelShapes.cuboid(0.25, 0.0, 0.0, 0.9375, 1.0, 1.0)
        )
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState) = GildedChestBlockEntity(pos, state, fake)

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape {
        return when (state.get(HorizontalFacingBlock.FACING)) {
            Direction.NORTH -> NORTH_OUTLINE
            Direction.SOUTH -> SOUTH_OUTLINE
            Direction.WEST -> WEST_OUTLINE
            else -> EAST_OUTLINE
        }
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        super.appendProperties(builder)
        builder.add(Properties.HORIZONTAL_FACING)
    }

    private val facingToYaw: HashMap<Direction, Float> = hashMapOf(
        Direction.SOUTH to -179.0F,
        Direction.EAST to 90.0F,
        Direction.NORTH to 0.0F,
        Direction.WEST to -90.0F
    )

    override fun getName() = Text.translatable("block.cobblemon.gilded_chest")

    override fun onBreak(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity) {
        if (fake) {
            spawnPokemon(world, pos, state, player)
            world.setBlockState(pos, Blocks.AIR.defaultState)
        } else super.onBreak(world, pos, state, player)
    }

    private fun spawnPokemon(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity) : ActionResult {
        val properties = "$POKEMON_ARGS lvl=${LEVEL_RANGE.random()}"
        val pokemon = PokemonProperties.parse(properties)
        val entity = pokemon.createEntity(world)
        entity.dataTracker.set(PokemonEntity.SPAWN_DIRECTION, facingToYaw[state[HorizontalFacingBlock.FACING]])
        entity.refreshPositionAndAngles(pos, entity.yaw, entity.pitch)
        world.spawnEntity(entity)
        world.playSound(null, pos, CobblemonSounds.GIMMIGHOUL_REVEAL, SoundCategory.NEUTRAL)

        world.removeBlock(pos, false)
        entity.forceBattle(player)
        return ActionResult.SUCCESS
    }

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ActionResult {
        if (fake) return spawnPokemon(world, pos, state, player)
        val entity = world.getBlockEntity(pos) as? GildedChestBlockEntity ?: return ActionResult.FAIL
        player.openHandledScreen(entity)
        val state = entity.poseableState
        state.currentModel?.let {
            it.moveToPose(null, state, it.getPose("OPEN")!!)
        }
        return ActionResult.SUCCESS
    }

    override fun onStateReplaced(
        state: BlockState,
        world: World,
        pos: BlockPos,
        newState: BlockState,
        moved: Boolean
    ) {
        if (!state.isOf(newState.block)) {
            val chest = world.getBlockEntity(pos) as GildedChestBlockEntity
            ItemScatterer.spawn(world, pos, chest.inventoryContents)
        }
    }

    override fun getRenderType(state: BlockState?) = BlockRenderType.ENTITYBLOCK_ANIMATED

    override fun getPlacementState(blockPlaceContext: ItemPlacementContext): BlockState? {
//        val abovePosition = blockPlaceContext.blockPos.up()
//        val world = blockPlaceContext.world
//        if (world.getBlockState(abovePosition).canReplace(blockPlaceContext) && !world.isOutOfHeightLimit(abovePosition)) {
            return defaultState.with(HorizontalFacingBlock.FACING, blockPlaceContext.horizontalPlayerFacing)
//        }
        // return null
    }

}