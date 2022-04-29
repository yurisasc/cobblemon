package com.cablemc.pokemoncobbled.common.api.abilities

import com.cablemc.pokemoncobbled.common.util.DataKeys
import com.google.gson.JsonObject
import net.minecraft.nbt.NbtCompound
import net.minecraft.text.Text

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

    val displayName: Text
        get() = template.displayName

    val description: Text
        get() = template.description

    open fun saveToNBT(nbt: NbtCompound): NbtCompound {
        nbt.putString(DataKeys.POKEMON_ABILITY_NAME, name)
        return nbt
    }

    open fun saveToJSON(json: JsonObject): JsonObject {
        json.addProperty(DataKeys.POKEMON_ABILITY_NAME, name)
        return json
    }

    open fun loadFromNBT(nbt: NbtCompound): Ability {
        return this
    }

    open fun loadFromJSON(json: JsonObject): Ability {
        return this
    }
}