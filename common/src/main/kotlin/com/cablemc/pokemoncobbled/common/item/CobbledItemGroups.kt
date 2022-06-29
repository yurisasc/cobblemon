package com.cablemc.pokemoncobbled.common.item

import com.cablemc.pokemoncobbled.common.CobbledItems
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import dev.architectury.registry.CreativeTabRegistry.create
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack

object CobbledItemGroups {
    val POKE_BALL_GROUP: ItemGroup = create(cobbledResource("pokeball")) { ItemStack(CobbledItems.POKE_BALL.get()) }
    val EVOLUTION_ITEM_GROUP: ItemGroup = create(cobbledResource("evolution_item")) { ItemStack(CobbledItems.BLACK_AUGURITE.get()) }
    val MEDICINE_ITEM_GROUP: ItemGroup = create(cobbledResource("medicine")) { ItemStack(CobbledItems.RARE_CANDY.get()) }
}