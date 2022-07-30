package com.cablemc.pokemoncobbled.common.api.abilities

import com.cablemc.pokemoncobbled.common.util.lang
import com.google.gson.JsonObject
import net.minecraft.nbt.NbtCompound
import net.minecraft.text.MutableText

/**
 * This represents the base of an Ability.
 * To build an Ability you MUST use its template.
 *
 * @param name: The English name used to load / find it (spaces -> _)
 */
class AbilityTemplate(
    val name: String,
    val builder: (AbilityTemplate) -> Ability = { Ability(it) },
    val displayName: MutableText = lang("ability.${name.lowercase()}"),
    val description: MutableText = lang("ability.${name.lowercase()}.desc")
) {
    /**
     * Returns the Ability or if applicable the extension connected to this template
     */
    fun create() = builder(this)


    /**
     * Returns the Ability and loads the given NBT Tag into it.
     *
     * Ability extensions need to write and read their needed data from here.
     */
    fun create(nbt: NbtCompound) = builder(this).loadFromNBT(nbt)

    /**
     * Returns the Ability and loads the given JSON object into it.
     *
     * Ability extensions need to write and read their needed data from here.
     */
    fun create(json: JsonObject) = builder(this).loadFromJSON(json)
}