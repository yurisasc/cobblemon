package com.cablemc.pokemoncobbled.common.api.moves.categories

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.resources.ResourceLocation

object DamageCategories {
    private val allCategories = mutableListOf<DamageCategory>()

    val PHYSICAL = register(
        name = "physical",
        displayName = TranslatableComponent("pokemoncobbled.move.category.physical"),
        resourceLocation = ResourceLocation(PokemonCobbled.MODID)
    )
    val SPECIAL = register(
        name = "special",
        displayName = TranslatableComponent("pokemoncobbled.move.category.special"),
        resourceLocation = ResourceLocation(PokemonCobbled.MODID)
    )
    val STATUS = register(
        name = "status",
        displayName = TranslatableComponent("pokemoncobbled.move.category.status"),
        resourceLocation = ResourceLocation(PokemonCobbled.MODID)
    )

    fun register(name: String, displayName: Component, resourceLocation: ResourceLocation): DamageCategory {
        return DamageCategory(
            name = name,
            displayName = displayName,
            resourceLocation = resourceLocation
        ).also {
            allCategories.add(it)
        }
    }

    fun register(damageCategory: DamageCategory): DamageCategory {
        allCategories.add(damageCategory)
        return damageCategory
    }

    fun get(name: String): DamageCategory? {
        return allCategories.firstOrNull { cat -> cat.name.equals(name, ignoreCase = true) }
    }

    fun getOrException(name: String): DamageCategory {
        return allCategories.first { cat -> cat.name.equals(name, ignoreCase = true) }
    }

    fun count() = allCategories.size
}