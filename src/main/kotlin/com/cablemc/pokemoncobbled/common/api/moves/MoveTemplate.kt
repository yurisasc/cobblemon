package com.cablemc.pokemoncobbled.common.api.moves

import com.cablemc.pokemoncobbled.common.api.moves.categories.DamageCategories
import com.cablemc.pokemoncobbled.common.api.types.ElementalTypes
import net.minecraft.network.chat.TranslatableComponent

data class MoveTemplate(
    val name: String,
    val type: String,
    val damageCategory: String,
    val power: Double,
    val accuracy: Double,
    val maxPp: Int
) {
    companion object {
        private const val PREFIX = "pokemoncobbled.move."

        fun nameComp(name: String): TranslatableComponent {
            return TranslatableComponent(PREFIX + name.lowercase())
        }

        fun descComp(name: String): TranslatableComponent {
            return TranslatableComponent(PREFIX + name.lowercase() + ".desc")
        }
    }

    fun move() = move(maxPp)

    fun move(currentPp: Int): Move {
        return Move(
            name = nameComp(name),
            description = descComp(name),
            type = ElementalTypes.getOrException(type),
            damageCategory = DamageCategories.getOrException(damageCategory),
            power = power,
            accuracy = accuracy,
            currentPp = currentPp,
            maxPp = maxPp
        )
    }
}
