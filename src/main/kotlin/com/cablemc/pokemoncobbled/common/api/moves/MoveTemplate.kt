package com.cablemc.pokemoncobbled.common.api.moves

import com.cablemc.pokemoncobbled.common.api.moves.categories.DamageCategory
import com.cablemc.pokemoncobbled.common.api.types.ElementalType
import net.minecraft.network.chat.TranslatableComponent

class MoveTemplate(
    val name: String,
    val elementalType: ElementalType,
    val damageCategory: DamageCategory,
    val power: Double,
    val accuracy: Double,
    val maxPp: Int
) {
    companion object {
        private const val PREFIX = "pokemoncobbled.move."

        fun nameComp(name: String) = TranslatableComponent(PREFIX + name.lowercase())
        fun descComp(name: String) = TranslatableComponent(PREFIX + name.lowercase() + ".desc")
    }

    @Transient
    val displayName = nameComp(name)
    @Transient
    val description = descComp(name)

    fun create() = create(maxPp)

    fun create(currentPp: Int): Move {
        return Move(
            currentPp = currentPp,
            maxPp = maxPp,
            template = this
        )
    }
}
