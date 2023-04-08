package com.cobblemon.mod.common.block

import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.block.entity.HealingMachineBlockEntity
import com.cobblemon.mod.common.block.entity.PokemonTetherBlockEntity
import com.cobblemon.mod.common.util.isInBattle
import com.cobblemon.mod.common.util.party
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.state.StateManager
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldAccess
import net.minecraft.world.WorldView

class PokemonTetherBlock(properties: Settings): BlockWithEntity(properties) {
    companion object {
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState) = PokemonTetherBlockEntity(pos, state)

    init {
        defaultState = this.stateManager.defaultState.with(HorizontalFacingBlock.FACING, Direction.NORTH)
//            .with(PCBlock.ON, false)
    }

    override fun getRenderType(state: BlockState) = BlockRenderType.MODEL
    override fun getPlacementState(blockPlaceContext: ItemPlacementContext) = defaultState
        .with(HorizontalFacingBlock.FACING, blockPlaceContext.playerFacing)

    override fun canPlaceAt(state: BlockState, world: WorldView, pos: BlockPos): Boolean {
        return true
    }

    @Deprecated("Deprecated in Java")
    override fun canPathfindThrough(blockState: BlockState, blockGetter: BlockView, blockPos: BlockPos, pathComputationType: NavigationType) = false
    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(HorizontalFacingBlock.FACING)
    }

    override fun onBroken(world: WorldAccess, pos: BlockPos, state: BlockState) {
        super.onBroken(world, pos, state)
        val blockEntity = world.getBlockEntity(pos) as? PokemonTetherBlockEntity ?: return
        blockEntity.releaseAllPokemon()
    }

    override fun <T : BlockEntity?> getTicker(world: World, state: BlockState, type: BlockEntityType<T>): BlockEntityTicker<T>? {
        return checkType(type, CobblemonBlockEntities.POKEMON_TETHER, PokemonTetherBlockEntity.TICKER::tick)
    }

    @Deprecated("Deprecated in Java")
    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ActionResult {
        if (player is ServerPlayerEntity && !player.isInBattle()) {
            val pokemon = player.party().first()
            val blockFacing = state.get(HorizontalFacingBlock.FACING)
            val blockEntity = world.getBlockEntity(pos) as? PokemonTetherBlockEntity ?: return ActionResult.FAIL

            blockEntity.tether(player, pokemon, blockFacing)
            return ActionResult.SUCCESS
        }

        return ActionResult.SUCCESS
    }
}