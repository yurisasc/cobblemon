package com.cobblemon.mod.common.gui.cooking

import com.google.common.collect.HashBasedTable
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.google.common.collect.Lists
import com.google.common.collect.Maps
import com.google.common.collect.Table
import com.mojang.logging.LogUtils
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection
import net.minecraft.recipe.AbstractCookingRecipe
import net.minecraft.recipe.CraftingRecipe
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeType
import net.minecraft.recipe.book.CookingRecipeCategory
import net.minecraft.recipe.book.CraftingRecipeCategory
import net.minecraft.recipe.book.RecipeBook
import net.minecraft.registry.DynamicRegistryManager
import net.minecraft.registry.Registries
import org.slf4j.Logger
import java.util.*

@Environment(EnvType.CLIENT)
class ClientCookBook : RecipeBook() {
    private val logger: Logger = LogUtils.getLogger()
    private var resultsByGroup: Map<CookBookGroup, List<RecipeResultCollection>> = ImmutableMap.of()
    private var orderedResults: List<RecipeResultCollection> = ImmutableList.of()

    fun reload(recipes: Iterable<Recipe<*>>, registryManager: DynamicRegistryManager) {
        val groupedMap = toGroupedMap(recipes)
        val map2 = Maps.newHashMap<CookBookGroup, List<RecipeResultCollection>>()
        val builder = ImmutableList.builder<RecipeResultCollection>()
        groupedMap.forEach { (recipeBookGroup, list) ->
            val var10002 = list.stream().map { recipes ->
                RecipeResultCollection(registryManager, recipes)
            }
            map2[recipeBookGroup] = var10002.peek { builder.add(it) }.collect(ImmutableList.toImmutableList())
        }
        CookBookGroup.SEARCH_MAP.forEach { (group, searchGroups) ->
            map2[group] = searchGroups.stream().flatMap { searchGroup ->
                map2.getOrDefault(searchGroup, ImmutableList.of()).stream()
            }.collect(ImmutableList.toImmutableList())
        }
        resultsByGroup = ImmutableMap.copyOf(map2)
        orderedResults = builder.build()
    }

    private fun toGroupedMap(recipes: Iterable<Recipe<*>>): Map<CookBookGroup, List<List<Recipe<*>>>> {
        val map = Maps.newHashMap<CookBookGroup, MutableList<List<Recipe<*>>>>()
        val table: Table<CookBookGroup, String, MutableList<Recipe<*>>> = HashBasedTable.create()
        for (recipe in recipes) {
            if (!recipe.isIgnoredInRecipeBook && !recipe.isEmpty) {
                val recipeBookGroup = getGroupForRecipe(recipe)
                val group = recipe.group
                if (group.isEmpty()) {
                    map.computeIfAbsent(recipeBookGroup) { Lists.newArrayList() }.add(ImmutableList.of(recipe))
                } else {
                    val list = table.get(recipeBookGroup, group) ?: Lists.newArrayList<Recipe<*>>().also { newList ->
                        table.put(recipeBookGroup, group, newList)
                        map.computeIfAbsent(recipeBookGroup) { Lists.newArrayList() }.add(newList)
                    }
                    list.add(recipe)
                }
            }
        }
        return map
    }

    private fun getGroupForRecipe(recipe: Recipe<*>): CookBookGroup {
        return when {
            recipe is CraftingRecipe -> {
                when (recipe.category) {
                    CraftingRecipeCategory.BUILDING -> CookBookGroup.CRAFTING_BUILDING_BLOCKS
                    CraftingRecipeCategory.EQUIPMENT -> CookBookGroup.CRAFTING_EQUIPMENT
                    CraftingRecipeCategory.REDSTONE -> CookBookGroup.CRAFTING_REDSTONE
                    CraftingRecipeCategory.MISC -> CookBookGroup.CRAFTING_MISC
                    else -> throw IncompatibleClassChangeError()
                }
            }
            recipe is AbstractCookingRecipe -> {
                val cookingRecipeCategory = recipe.category
                when (recipe.type) {
                    RecipeType.SMELTING -> {
                        when (cookingRecipeCategory) {
                            CookingRecipeCategory.BLOCKS -> CookBookGroup.FURNACE_BLOCKS
                            CookingRecipeCategory.FOOD -> CookBookGroup.FURNACE_FOOD
                            CookingRecipeCategory.MISC -> CookBookGroup.FURNACE_MISC
                            else -> throw IncompatibleClassChangeError()
                        }
                    }
                    RecipeType.BLASTING -> {
                        if (cookingRecipeCategory == CookingRecipeCategory.BLOCKS) CookBookGroup.BLAST_FURNACE_BLOCKS else CookBookGroup.BLAST_FURNACE_MISC
                    }
                    RecipeType.SMOKING -> CookBookGroup.SMOKER_FOOD
                    RecipeType.CAMPFIRE_COOKING -> CookBookGroup.CAMPFIRE
                    else -> {
                        logger.warn("Unknown recipe category: {}/{}", LogUtils.defer { Registries.RECIPE_TYPE.getId(recipe.type) }, LogUtils.defer { recipe.id })
                        CookBookGroup.UNKNOWN
                    }
                }
            }
            recipe.type == RecipeType.STONECUTTING -> CookBookGroup.STONECUTTER
            recipe.type == RecipeType.SMITHING -> CookBookGroup.SMITHING
            else -> {
                logger.warn("Unknown recipe category: {}/{}", LogUtils.defer { Registries.RECIPE_TYPE.getId(recipe.type) }, LogUtils.defer { recipe.id })
                CookBookGroup.UNKNOWN
            }
        }
    }

    fun getOrderedResults(): List<RecipeResultCollection> {
        return orderedResults
    }

    fun getResultsForGroup(category: CookBookGroup): List<RecipeResultCollection> {
        return resultsByGroup.getOrDefault(category, Collections.emptyList())
    }
}
