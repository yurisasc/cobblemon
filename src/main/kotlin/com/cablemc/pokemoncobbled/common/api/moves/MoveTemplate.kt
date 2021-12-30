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
    val effectChance: Double,
    val maxPp: Int
) {
    @Transient
    lateinit var displayName: TranslatableComponent
    @Transient
    lateinit var description: TranslatableComponent

    fun create() = create(maxPp)

    fun create(currentPp: Int): Move {
        return Move(
            currentPp = currentPp,
            maxPp = maxPp,
            template = this
        )
    }

    fun createTextComponents() {
        val prefix = "pokemoncobbled.move."
        displayName = TranslatableComponent(prefix + name.lowercase())
        description = TranslatableComponent(prefix + name.lowercase() + ".desc")
    }
}
