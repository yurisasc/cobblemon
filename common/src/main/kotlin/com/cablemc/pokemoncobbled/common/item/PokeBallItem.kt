package com.cablemc.pokemoncobbled.common.item

import com.cablemc.pokemoncobbled.common.entity.pokeball.EmptyPokeBallEntity
import com.cablemc.pokemoncobbled.common.item.creativetabs.PokeBallTab
import com.cablemc.pokemoncobbled.common.pokeball.PokeBall
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class PokeBallItem(
    val pokeBall : PokeBall
) : CobbledItem(Properties().tab(PokeBallTab)) {

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val itemStack = player.getItemInHand(usedHand)
        if (!level.isClientSide) {
            throwPokeBall(level, player as ServerPlayer)
        }
        if (!player.abilities.instabuild) {
            itemStack.shrink(1)
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide)
    }

    private fun throwPokeBall(level: Level, player: ServerPlayer) {
        val pokeBallEntity = EmptyPokeBallEntity(pokeBall, player.level).apply {
            setPos(player.x, player.y + 1.5, player.z)
            shootFromRotation(player, player.xRot - 7, player.yRot, 0.0f, 1.5f, 1.0f)
            owner = player
        }
        level.addFreshEntity(pokeBallEntity)
    }
}