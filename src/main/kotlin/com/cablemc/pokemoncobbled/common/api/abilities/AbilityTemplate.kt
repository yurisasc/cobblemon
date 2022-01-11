package com.cablemc.pokemoncobbled.common.api.abilities

import com.cablemc.pokemoncobbled.common.api.abilities.extensions.AbilityExtensions
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.TranslatableComponent

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
    lateinit var displayName: TranslatableComponent
    @Transient
    lateinit var description: TranslatableComponent

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
    fun create(nbt: CompoundTag): Ability {
        if (AbilityExtensions.contains(name)) {
            return AbilityExtensions.get(name)!!.getConstructor().newInstance().also {
                it.loadFromNBT(nbt)
            }
        }
        return Ability(this).also {
            it.loadFromNBT(nbt)
        }
    }

    /**
     * Creates the Components needed to display the Move and its Description
     */
    fun createTextComponents() {
        displayName = TranslatableComponent(PREFIX + name.lowercase())
        description = TranslatableComponent(PREFIX + name.lowercase() + ".desc")
    }
}