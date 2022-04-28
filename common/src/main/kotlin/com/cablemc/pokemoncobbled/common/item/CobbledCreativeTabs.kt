package com.cablemc.pokemoncobbled.common.item

import com.cablemc.pokemoncobbled.common.CobbledItems
import com.cablemc.pokemoncobbled.common.util.asResource
import dev.architectury.registry.CreativeTabRegistry.create
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack

object CobbledCreativeTabs {
    val POKE_BALL_TAB: CreativeModeTab = create(cobbledResource("pokeball")) { ItemStack(CobbledItems.POKE_BALL) }
    val EVOLUTION_ITEM_TAB: CreativeModeTab = create(cobbledResource("evolution_item")) { ItemStack(CobbledItems.BLACK_AUGURITE) }
}