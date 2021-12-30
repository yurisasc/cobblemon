package com.cablemc.pokemoncobbled.common.api.moves

import com.cablemc.pokemoncobbled.common.api.moves.categories.DamageCategory
import com.cablemc.pokemoncobbled.common.api.types.ElementalType
import net.minecraft.network.chat.Component

class Move(
    var currentPp: Int,
    val maxPp: Int,
    val template: MoveTemplate
) {
    val name: String
        get() = template.name

    val displayName: Component
        get() = template.displayName

    val description: Component
        get() = template.description

    val type: ElementalType
        get() = template.elementalType

    val damageCategory: DamageCategory
        get() = template.damageCategory

    val power: Double
        get() = template.power

    val accuracy: Double
        get() = template.accuracy
}