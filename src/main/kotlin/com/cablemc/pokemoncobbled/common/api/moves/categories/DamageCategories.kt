package com.cablemc.pokemoncobbled.common.api.moves.categories

import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.resources.ResourceLocation

object DamageCategories {
    private val allCategories = mutableListOf<DamageCategory>()

    val PHYSICAL = register(
        name = "physical",
        displayName = TranslatableComponent("pokemoncobbled.move.category.physical"),
        textureXMultiplier = 0
    )
    val SPECIAL = register(
        name = "special",
        displayName = TranslatableComponent("pokemoncobbled.move.category.special"),
        textureXMultiplier = 1
    )
    val STATUS = register(
        name = "status",
        displayName = TranslatableComponent("pokemoncobbled.move.category.status"),
        textureXMultiplier = 2
    )

    fun register(
        name: String,
        displayName: Component,
        resourceLocation: ResourceLocation = cobbledResource("ui/categories.png"),
        textureXMultiplier: Int
    ): DamageCategory {
        return DamageCategory(
            name = name,
            displayName = displayName,
            textureXMultiplier = textureXMultiplier,
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