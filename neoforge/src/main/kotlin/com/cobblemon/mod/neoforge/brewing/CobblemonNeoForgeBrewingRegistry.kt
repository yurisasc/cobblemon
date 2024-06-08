/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.neoforge.brewing

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.brewing.BrewingRecipes
import com.cobblemon.mod.common.brewing.ingredient.CobblemonIngredient
import com.cobblemon.mod.common.brewing.ingredient.CobblemonItemIngredient
import com.cobblemon.mod.common.brewing.ingredient.CobblemonPotionIngredient
import com.mojang.serialization.Codec
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.potion.Potions
import net.minecraft.recipe.Ingredient
import net.minecraft.util.Identifier
import net.neoforged.neoforge.common.brewing.BrewingRecipe
import net.neoforged.neoforge.common.brewing.BrewingRecipeRegistry
import net.neoforged.neoforge.common.brewing.IBrewingRecipe
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent

internal object CobblemonNeoForgeBrewingRegistry {

    fun register(e: RegisterBrewingRecipesEvent) {
        this.registerIngredientTypes()
        this.registerRecipes(e)
    }

    private fun registerIngredientTypes() {
        //CraftingHelper.register(cobblemonResource("potion"), ForgePotionIngredientSerializer)
    }

    private fun registerRecipes(e: RegisterBrewingRecipesEvent) {
        BrewingRecipes.recipes.forEach { (input, ingredient, output) ->
            e.builder.addRecipe(
                object : IBrewingRecipe {
                    override fun isInput(arg: ItemStack): Boolean {
                        if (input is CobblemonItemIngredient) {
                            return input.item == arg.item
                        }
                        else if (input is CobblemonPotionIngredient) {
                            return input.matches(arg)
                        }
                        else {
                            return false
                        }
                    }

                    override fun isIngredient(arg: ItemStack): Boolean {
                        if (input is CobblemonItemIngredient) {
                            return input.item == arg.item
                        }
                        else if (input is CobblemonPotionIngredient) {
                            return input.matches(arg)
                        }
                        else {
                            return false
                        }
                    }

                    override fun getOutput(arg: ItemStack, arg2: ItemStack): ItemStack {
                        return output.defaultStack
                    }

                }
            )
        }
    }


}