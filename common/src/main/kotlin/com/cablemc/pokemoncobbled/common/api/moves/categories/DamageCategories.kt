package com.cablemc.pokemoncobbled.common.api.moves.categories

import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.text.Text
import net.minecraft.util.Identifier

object DamageCategories {
    private val allCategories = mutableListOf<DamageCategory>()

    val PHYSICAL = register(
        name = "physical",
        displayName = Text.translatable("pokemoncobbled.move.category.physical"),
        textureXMultiplier = 0
    )
    val SPECIAL = register(
        name = "special",
        displayName = Text.translatable("pokemoncobbled.move.category.special"),
        textureXMultiplier = 1
    )
    val STATUS = register(
        name = "status",
        displayName = Text.translatable("pokemoncobbled.move.category.status"),
        textureXMultiplier = 2
    )

    fun register(
        name: String,
        displayName: Text,
        resourceLocation: Identifier = cobbledResource("ui/categories.png"),
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