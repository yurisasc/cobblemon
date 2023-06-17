/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.brewing

import com.cobblemon.mod.common.CobblemonItems
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.potion.Potion
import net.minecraft.potion.Potions
import net.minecraft.recipe.BrewingRecipeRegistry
import net.minecraft.recipe.Ingredient

object BrewingRecipes {
    // Put things here if you want them to be add-able to the bottom of the brewing stand (like where potions go)
    @JvmField
    val brewableItems = mutableListOf<Item>(
        CobblemonItems.MEDICINAL_BREW,
        CobblemonItems.ZINC
    )

    fun registerPotionTypes() {
        brewableItems.forEach { item ->
            BrewingRecipeRegistry.POTION_TYPES.add(Ingredient.ofItems(item))
        }
    }

    fun getPotionRecipes(): List<Triple<Potion, Ingredient, Potion>> {
        return listOf(
            Triple(Potions.WATER, Ingredient.ofItems(CobblemonItems.MEDICINAL_LEEK), Potions.WATER) // This one is Mixin'd to result in brew rather than water
        )
    }

    fun getItemRecipes(): List<Triple<Item, Ingredient, Item>> {
        // This is where the majority of our recipes go. First arg is thing down the bottom, second is thing up the top, third is results.
        return listOf(
            Triple(CobblemonItems.MEDICINAL_BREW, Ingredient.ofItems(Items.GOLD_INGOT), CobblemonItems.ZINC)
        )
    }
}
