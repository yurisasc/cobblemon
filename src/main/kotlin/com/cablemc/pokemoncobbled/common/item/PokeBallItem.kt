package com.cablemc.pokemoncobbled.common.item

import com.cablemc.pokemoncobbled.common.entity.pokeball.EmptyPokeBallEntity
import com.cablemc.pokemoncobbled.common.item.creativetabs.PokeBallTab
import com.cablemc.pokemoncobbled.common.pokemon.pokeball.PokeBall
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
            throwPokeBall(level, player)
        }
        if (!player.abilities.instabuild) {
            itemStack.shrink(1)
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide)
    }

    private fun throwPokeBall(level: Level, player: Player) {
        val pokeBallEntity = EmptyPokeBallEntity(pokeBall, player.level).apply {
            setPos(player.x, player.y, player.z)
            shootFromRotation(player, player.xRot - 7, player.yRot, 0.0f, 1.5f, 1.0f)
        }
        level.addFreshEntity(pokeBallEntity)
    }

}