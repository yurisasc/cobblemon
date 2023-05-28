package com.cobblemon.mod.common.brewing

import com.cobblemon.mod.common.CobblemonItems
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.recipe.BrewingRecipeRegistry
import net.minecraft.recipe.Ingredient

object BrewingRecipes {
    fun registerPotionTypes() {
        BrewingRecipeRegistry.POTION_TYPES.add(Ingredient.ofItems(CobblemonItems.POKE_BALL))
    }

    fun getRecipes(): List<Triple<Item, Ingredient, Item>> {
        return listOf(
            Triple(Items.POTION, Ingredient.ofItems(Items.GOLD_INGOT), CobblemonItems.POKE_BALL),
            Triple(Items.POTION, Ingredient.ofItems(Items.GOLD_INGOT), CobblemonItems.POKE_BALL),
            Triple(CobblemonItems.POKE_BALL, Ingredient.ofItems(Items.IRON_INGOT), CobblemonItems.GREAT_BALL)
        )
    }
}
