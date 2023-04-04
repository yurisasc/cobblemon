package com.cobblemon.mod.common.brewing

import com.cobblemon.mod.common.CobblemonItems
import net.minecraft.item.Items
import net.minecraft.recipe.BrewingRecipeRegistry
import net.minecraft.recipe.Ingredient

object BrewingRecipes {
    fun registerBrewingRecipes() {
        BrewingRecipeRegistry.POTION_TYPES.add(Ingredient.ofItems(CobblemonItems.POKE_BALL))
        BrewingRecipeRegistry.ITEM_RECIPES.add(BrewingRecipeRegistry.Recipe(Items.POTION, Ingredient.ofItems(Items.GOLD_INGOT), CobblemonItems.POKE_BALL))
        BrewingRecipeRegistry.ITEM_RECIPES.add(BrewingRecipeRegistry.Recipe(CobblemonItems.POKE_BALL, Ingredient.ofItems(Items.IRON_INGOT), CobblemonItems.GREAT_BALL))
    }
}
