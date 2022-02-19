package com.cablemc.pokemoncobbled.common.item.creativetabs

import com.cablemc.pokemoncobbled.common.CobbledItems
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack

object PokeBallTab: CreativeModeTab("pokemoncobbled.pokeballtab") {
    override fun makeIcon(): ItemStack {
        return ItemStack(CobbledItems.POKE_BALL_TYPE)
    }
}