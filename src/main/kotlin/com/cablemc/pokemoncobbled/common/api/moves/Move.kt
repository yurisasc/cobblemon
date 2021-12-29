package com.cablemc.pokemoncobbled.common.api.moves

import com.cablemc.pokemoncobbled.common.api.moves.categories.DamageCategory
import com.cablemc.pokemoncobbled.common.api.types.ElementalType
import net.minecraft.network.chat.Component

class Move(
    val name: Component,
    val description: Component,
    val type: ElementalType,
    val damageCategory: DamageCategory,
    val power: Double,
    val accuracy: Double,
    var currentPp: Int,
    val maxPp: Int
) {
}