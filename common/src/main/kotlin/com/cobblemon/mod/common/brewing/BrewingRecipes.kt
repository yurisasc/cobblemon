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
import net.minecraft.potion.Potion
import net.minecraft.potion.Potions
import net.minecraft.recipe.BrewingRecipeRegistry
import net.minecraft.recipe.Ingredient

object BrewingRecipes {
    // Put things here if you want them to be add-able to the bottom of the brewing stand (like where potions go)
    @JvmField
    val brewableItems = mutableListOf(
        CobblemonItems.MEDICINAL_BREW,
        CobblemonItems.POTION,
        CobblemonItems.SUPER_POTION,
        CobblemonItems.HYPER_POTION,
        CobblemonItems.MAX_POTION,
        CobblemonItems.FULL_RESTORE,
        CobblemonItems.FULL_HEAL,
        CobblemonItems.ANTIDOTE,
        CobblemonItems.AWAKENING,
        CobblemonItems.BURN_HEAL,
        CobblemonItems.ICE_HEAL,
        CobblemonItems.PARALYZE_HEAL,
        CobblemonItems.ETHER,
        CobblemonItems.MAX_ETHER,
        CobblemonItems.ELIXIR,
        CobblemonItems.MAX_ELIXIR
    )

    fun registerPotionTypes() {
        brewableItems.forEach { item ->
            BrewingRecipeRegistry.POTION_TYPES.add(Ingredient.ofItems(item))
        }
    }

    fun getPotionRecipes(): List<Triple<Potion, Ingredient, Potion>> {
        return listOf(
            Triple(Potions.WATER, Ingredient.ofItems(CobblemonItems.MEDICINAL_LEEK), Potions.WATER), // This one is Mixin'd to result in brew rather than water
        )
    }

    fun getItemRecipes(): List<Triple<Item, Ingredient, Item>> {
        // This is where the majority of our recipes go. First arg is thing down the bottom, second is thing up the top, third is results.
        return listOf(
            Triple(CobblemonItems.MEDICINAL_BREW, Ingredient.ofItems(CobblemonItems.LEPPA_BERRY), CobblemonItems.ETHER),
            Triple(CobblemonItems.MEDICINAL_BREW, Ingredient.ofItems(CobblemonItems.HOPO_BERRY), CobblemonItems.ELIXIR),
            Triple(CobblemonItems.MEDICINAL_BREW, Ingredient.ofItems(CobblemonItems.ORAN_BERRY), CobblemonItems.POTION),
            Triple(CobblemonItems.MEDICINAL_BREW, Ingredient.ofItems(CobblemonItems.SITRUS_BERRY), CobblemonItems.HYPER_POTION),
            Triple(CobblemonItems.MEDICINAL_BREW, Ingredient.ofItems(CobblemonItems.LUM_BERRY), CobblemonItems.FULL_HEAL),
            Triple(CobblemonItems.MEDICINAL_BREW, Ingredient.ofItems(CobblemonItems.PECHA_BERRY), CobblemonItems.ANTIDOTE),
            Triple(CobblemonItems.MEDICINAL_BREW, Ingredient.ofItems(CobblemonItems.CHESTO_BERRY), CobblemonItems.AWAKENING),
            Triple(CobblemonItems.MEDICINAL_BREW, Ingredient.ofItems(CobblemonItems.RAWST_BERRY), CobblemonItems.BURN_HEAL),
            Triple(CobblemonItems.MEDICINAL_BREW, Ingredient.ofItems(CobblemonItems.ASPEAR_BERRY), CobblemonItems.ICE_HEAL),
            Triple(CobblemonItems.MEDICINAL_BREW, Ingredient.ofItems(CobblemonItems.CHERI_BERRY), CobblemonItems.PARALYZE_HEAL),
            Triple(CobblemonItems.BURN_HEAL, Ingredient.ofItems(CobblemonItems.LUM_BERRY), CobblemonItems.FULL_HEAL),
            Triple(CobblemonItems.ANTIDOTE, Ingredient.ofItems(CobblemonItems.LUM_BERRY), CobblemonItems.FULL_HEAL),
            Triple(CobblemonItems.AWAKENING, Ingredient.ofItems(CobblemonItems.LUM_BERRY), CobblemonItems.FULL_HEAL),
            Triple(CobblemonItems.ICE_HEAL, Ingredient.ofItems(CobblemonItems.LUM_BERRY), CobblemonItems.FULL_HEAL),
            Triple(CobblemonItems.PARALYZE_HEAL, Ingredient.ofItems(CobblemonItems.LUM_BERRY), CobblemonItems.FULL_HEAL),
            Triple(CobblemonItems.ETHER, Ingredient.ofItems(CobblemonItems.PEP_UP_FLOWER), CobblemonItems.MAX_ETHER),
            Triple(CobblemonItems.ELIXIR, Ingredient.ofItems(CobblemonItems.PEP_UP_FLOWER), CobblemonItems.MAX_ELIXIR),
            Triple(CobblemonItems.POTION, Ingredient.ofItems(CobblemonItems.ENERGY_ROOT), CobblemonItems.SUPER_POTION),
            Triple(CobblemonItems.SUPER_POTION, Ingredient.ofItems(CobblemonItems.FIGY_BERRY), CobblemonItems.HYPER_POTION),
            Triple(CobblemonItems.SUPER_POTION, Ingredient.ofItems(CobblemonItems.WIKI_BERRY), CobblemonItems.HYPER_POTION),
            Triple(CobblemonItems.SUPER_POTION, Ingredient.ofItems(CobblemonItems.MAGO_BERRY), CobblemonItems.HYPER_POTION),
            Triple(CobblemonItems.SUPER_POTION, Ingredient.ofItems(CobblemonItems.AGUAV_BERRY), CobblemonItems.HYPER_POTION),
            Triple(CobblemonItems.SUPER_POTION, Ingredient.ofItems(CobblemonItems.IAPAPA_BERRY), CobblemonItems.HYPER_POTION),
            Triple(CobblemonItems.HYPER_POTION, Ingredient.ofItems(CobblemonItems.VIVICHOKE), CobblemonItems.MAX_POTION),
            Triple(CobblemonItems.MAX_POTION, Ingredient.ofItems(CobblemonItems.LUM_BERRY), CobblemonItems.FULL_RESTORE)
        )
    }
}
