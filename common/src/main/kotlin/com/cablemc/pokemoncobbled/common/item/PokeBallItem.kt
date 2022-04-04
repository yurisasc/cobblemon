package com.cablemc.pokemoncobbled.common.item

import com.cablemc.pokemoncobbled.common.entity.pokeball.EmptyPokeBallEntity
import com.cablemc.pokemoncobbled.common.item.CobbledCreativeTabs.POKE_BALL_TAB
import com.cablemc.pokemoncobbled.common.pokeball.PokeBall
import com.cablemc.pokemoncobbled.common.util.math.geometry.toRadians
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import kotlin.math.cos

class PokeBallItem(
    val pokeBall : PokeBall
) : CobbledItem(Properties().tab(POKE_BALL_TAB)) {

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
            setPos(player.x, player.y + player.eyeHeight - 0.2, player.z)
            shootFromRotation(player, player.xRot - 10 * cos(player.xRot.toRadians()), player.yRot, 0.0f, 1.25f, 1.0f)
            owner = player
        }
        level.addFreshEntity(pokeBallEntity)
    }
}