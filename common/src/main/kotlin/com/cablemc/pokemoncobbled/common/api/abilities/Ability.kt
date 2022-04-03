package com.cablemc.pokemoncobbled.common.api.abilities

import com.cablemc.pokemoncobbled.common.util.DataKeys
import com.google.gson.JsonObject
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component

/**
 * Representing an Ability with all its attributes
 *
 * Can be extended to allow for custom attributes (be sure to overwrite the load and save methods)
 */
open class Ability internal constructor(
    var template: AbilityTemplate
) {

    internal constructor() : this(fallbackTemplate)

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

    open fun saveToJSON(json: JsonObject): JsonObject {
        json.addProperty(DataKeys.POKEMON_ABILITY_NAME, name)
        return json
    }

    open fun loadFromNBT(nbt: CompoundTag): Ability {
        return this
    }

    open fun loadFromJSON(json: JsonObject): Ability {
        return this
    }
}