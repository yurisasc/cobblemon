package com.cobblemon.mod.common.api.events.pokemon.interaction

import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.item.ItemStack

data class HeldItemUpdatedEvent(
    val pokemon: Pokemon,
    val oldItem: ItemStack,
    val newItem: ItemStack
)