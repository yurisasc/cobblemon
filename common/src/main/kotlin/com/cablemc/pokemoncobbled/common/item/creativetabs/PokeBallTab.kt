package com.cablemc.pokemoncobbled.common.item.creativetabs

import com.cablemc.pokemoncobbled.common.item.ItemRegistry
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack

object PokeBallTab: CreativeModeTab("pokemoncobbled.pokeballtab") {
    override fun makeIcon(): ItemStack {
        return ItemStack(ItemRegistry.POKE_BALL.get())
    }
}