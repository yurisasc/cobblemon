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

    companion object {
        const val PREFIX = "pokemoncobbled.move."
    }

    fun create() = create(maxPp)

    fun create(currentPp: Int) = create(currentPp, maxPp)

    fun create(currentPp: Int, pMaxPp: Int): Move {
        return Move(
            currentPp = currentPp,
            maxPp = pMaxPp,
            template = this
        )
    }

    fun createTextComponents() {
        displayName = TranslatableComponent(PREFIX + name.lowercase())
        description = TranslatableComponent(PREFIX + name.lowercase() + ".desc")
    }
}
