package com.cablemc.pokemoncobbled.common.api.abilities

import com.cablemc.pokemoncobbled.common.util.DataKeys
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component

open class Ability(
    var template: AbilityTemplate
) {

    constructor() : this(fallbackTemplate)

    companion object {
        val fallbackTemplate = AbilityTemplate(
            name = "Fallback"
        )
    }

    val name: String
        get() = template.name

    val displayName: Component
        get() = template.displayName

    val description: Component
        get() = template.description

    open fun saveToNBT(nbt: CompoundTag): CompoundTag {
        nbt.putString(DataKeys.POKEMON_ABILITY_NAME, name)
        return nbt
    }

    open fun loadFromNBT(nbt: CompoundTag): Ability {
        return this
    }
}