package com.cablemc.pokemoncobbled.common.api.abilities

import com.cablemc.pokemoncobbled.common.api.abilities.extensions.AbilityExtensions
import com.google.gson.JsonObject
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.chat.TranslatableText

/**
 * This represents the base of an Ability.
 * To build an Ability you MUST use its template.
 *
 * @param name: The English name used to load / find it (spaces -> _)
 */
class AbilityTemplate(
    val name: String
) {
    @Transient
    lateinit var displayName: TranslatableText
    @Transient
    lateinit var description: TranslatableText

    companion object {
        const val PREFIX = "pokemoncobbled.ability."
    }

    /**
     * Returns the Ability or if applicable the extension connected to this template
     */
    fun create(): Ability {
        if (AbilityExtensions.contains(name)) {
            return AbilityExtensions.get(name)!!.getConstructor().newInstance()
        }
        return Ability(this)
    }


    /**
     * Returns the Ability and loads the given NBT Tag into it.
     *
     * Ability extensions need to write and read their needed data from here.
     */
    fun create(nbt: NbtCompound): Ability {
        return (AbilityExtensions.get(name)?.getDeclaredConstructor()?.newInstance() ?: Ability(this)).loadFromNBT(nbt)
    }

    /**
     * Returns the Ability and loads the given JSON object into it.
     *
     * Ability extensions need to write and read their needed data from here.
     */
    fun create(json: JsonObject): Ability {
        return (AbilityExtensions.get(name)?.getDeclaredConstructor()?.newInstance() ?: Ability(this)).loadFromJSON(json)
    }

    /**
     * Creates the Components needed to display the Move and its Description
     */
    fun createLiteralTexts() {
        displayName = TranslatableText(PREFIX + name.lowercase())
        description = TranslatableText(PREFIX + name.lowercase() + ".desc")
    }
}