package com.cobblemon.mod.common.gui.cooking

import com.google.common.collect.Sets
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.book.RecipeBook
import net.minecraft.recipe.book.RecipeBookCategory
import net.minecraft.recipe.book.RecipeBookOptions
import net.minecraft.screen.AbstractRecipeScreenHandler
import net.minecraft.util.Identifier
import org.jetbrains.annotations.Nullable

class CookBook {
    protected val recipes: MutableSet<Identifier> = Sets.newHashSet()
    protected val toBeDisplayed: MutableSet<Identifier> = Sets.newHashSet()
    private val options = RecipeBookOptions()

    fun copyFrom(book: CookBook) {
        recipes.clear()
        toBeDisplayed.clear()
        options.copyFrom(book.options)
        recipes.addAll(book.recipes)
        toBeDisplayed.addAll(book.toBeDisplayed)
    }

    fun add(recipe: Recipe<*>) {
        if (!recipe.isIgnoredInRecipeBook) {
            add(recipe.id)
        }
    }

    protected fun add(id: Identifier) {
        recipes.add(id)
    }

    fun contains(@Nullable recipe: Recipe<*>?): Boolean {
        return recipe?.let { recipes.contains(it.id) } ?: false
    }

    fun contains(id: Identifier): Boolean {
        return recipes.contains(id)
    }

    fun remove(recipe: Recipe<*>) {
        remove(recipe.id)
    }

    protected fun remove(id: Identifier) {
        recipes.remove(id)
        toBeDisplayed.remove(id)
    }

    fun shouldDisplay(recipe: Recipe<*>): Boolean {
        return toBeDisplayed.contains(recipe.id)
    }

    fun onRecipeDisplayed(recipe: Recipe<*>) {
        toBeDisplayed.remove(recipe.id)
    }

    fun display(recipe: Recipe<*>) {
        display(recipe.id)
    }

    protected fun display(id: Identifier) {
        toBeDisplayed.add(id)
    }

    fun isGuiOpen(category: RecipeBookCategory): Boolean {
        return options.isGuiOpen(category)
    }

    fun setGuiOpen(category: RecipeBookCategory, open: Boolean) {
        options.setGuiOpen(category, open)
    }

    fun isFilteringCraftable(handler: AbstractRecipeScreenHandler<*>): Boolean {
        return isFilteringCraftable(handler.category)
    }

    fun isFilteringCraftable(category: RecipeBookCategory): Boolean {
        return options.isFilteringCraftable(category)
    }

    fun setFilteringCraftable(category: RecipeBookCategory, filteringCraftable: Boolean) {
        options.setFilteringCraftable(category, filteringCraftable)
    }

    fun setOptions(options: RecipeBookOptions) {
        this.options.copyFrom(options)
    }

    fun getOptions(): RecipeBookOptions {
        return options.copy()
    }

    fun setCategoryOptions(category: RecipeBookCategory, guiOpen: Boolean, filteringCraftable: Boolean) {
        options.setGuiOpen(category, guiOpen)
        options.setFilteringCraftable(category, filteringCraftable)
    }
}
