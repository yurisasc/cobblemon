package com.cablemc.pokemoncobbled.common.item

import com.cablemc.pokemoncobbled.common.CobbledItems
import com.cablemc.pokemoncobbled.common.util.asResource
import dev.architectury.registry.CreativeTabRegistry.create
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack

object CobbledItemGroups {
    val POKE_BALL_TAB: ItemGroup = create("pokemoncobbled.pokeballtab".asResource()) { ItemStack(CobbledItems.POKE_BALL.get()) }
    val EVOLUTION_ITEM_TAB: ItemGroup = create("pokemoncobbled.evolution_item".asResource()) { ItemStack(CobbledItems.BLACK_AUGURITE.get()) }
}