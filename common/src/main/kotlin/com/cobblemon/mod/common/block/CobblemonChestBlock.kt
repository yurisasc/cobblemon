package com.cobblemon.mod.common.block

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.block.entity.CobblemonChestBlockEntity
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.ChestBlock
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.block.entity.ChestBlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import java.util.function.Supplier

class CobblemonChestBlock(settings: Settings, val isFake: Boolean, supplier: Supplier<BlockEntityType<out ChestBlockEntity>>) : ChestBlock(settings, supplier) {

    val facingToYaw: HashMap<Direction, Float> = hashMapOf(
        Direction.NORTH to -179.0F,
        Direction.WEST to 90.0F,
        Direction.SOUTH to 0.0F,
        Direction.EAST to -90.0F
    )

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = CobblemonChestBlockEntity(pos, state)

    @Deprecated("Deprecated in Java")
    override fun getRenderType(state: BlockState?): BlockRenderType = BlockRenderType.MODEL

    @Deprecated("Deprecated in Java")
    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        return if (isFake) spawnGimmighoul(world, pos, player, state) else super.onUse(state, world, pos, player, hand, hit)
    }

    private fun spawnGimmighoul(world: World, pos: BlockPos, player: PlayerEntity, state: BlockState) : ActionResult {
        val pokemon = PokemonProperties.parse("gimmighoul level=15")
        val entity = pokemon.createEntity(world)
        entity.refreshPositionAndAngles(pos, entity.yaw, entity.pitch)
        entity.spawnDirection.set(facingToYaw[state[FACING]])
        world.spawnEntity(entity)
        world.playSound(null, pos, CobblemonSounds.GIMMIGHOUL_REVEAL, SoundCategory.BLOCKS)

        world.removeBlock(pos, false)
        return ActionResult.SUCCESS
    }

    override fun onBreak(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity) {
        if (isFake) spawnGimmighoul(world, pos, player, state) else return super.onBreak(world, pos, state, player)
    }

}