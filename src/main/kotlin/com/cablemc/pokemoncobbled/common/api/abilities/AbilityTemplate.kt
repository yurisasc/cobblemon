package com.cablemc.pokemoncobbled.common.api.abilities

import com.cablemc.pokemoncobbled.common.api.abilities.extensions.AbilityExtensions
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.TranslatableComponent

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

    fun create(): Ability {
        if (AbilityExtensions.contains(name)) {
            return AbilityExtensions.get(name)!!.getConstructor().newInstance()
        }
        return Ability(this)
    }

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

    fun createTextComponents() {
        displayName = TranslatableComponent(PREFIX + name.lowercase())
        description = TranslatableComponent(PREFIX + name.lowercase() + ".desc")
    }
}