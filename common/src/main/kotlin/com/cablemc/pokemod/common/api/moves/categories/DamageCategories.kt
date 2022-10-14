/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.moves.categories

import com.cablemc.pokemod.common.util.pokemodResource
import net.minecraft.text.Text
import net.minecraft.util.Identifier

object DamageCategories {
    private val allCategories = mutableListOf<DamageCategory>()

    val PHYSICAL = register(
        name = "physical",
        displayName = Text.translatable("pokemod.move.category.physical"),
        textureXMultiplier = 0
    )
    val SPECIAL = register(
        name = "special",
        displayName = Text.translatable("pokemod.move.category.special"),
        textureXMultiplier = 1
    )
    val STATUS = register(
        name = "status",
        displayName = Text.translatable("pokemod.move.category.status"),
        textureXMultiplier = 2
    )

    fun register(
        name: String,
        displayName: Text,
        resourceLocation: Identifier = pokemodResource("ui/categories.png"),
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