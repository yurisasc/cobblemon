package com.cobblemon.mod.common.gui.cooking

import com.google.common.collect.Sets
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.book.RecipeBook
import net.minecraft.recipe.book.RecipeBookCategory
import net.minecraft.recipe.book.RecipeBookOptions
import net.minecraft.screen.AbstractRecipeScreenHandler
import net.minecraft.util.Identifier
import org.jetbrains.annotations.Nullable

class CookBook : RecipeBook() {
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

    override fun add(recipe: Recipe<*>) {
        if (!recipe.isIgnoredInRecipeBook) {
            add(recipe.id)
        }
    }

    override fun add(id: Identifier) {
        recipes.add(id)
    }

    override fun contains(@Nullable recipe: Recipe<*>?): Boolean {
        return recipe?.let { recipes.contains(it.id) } ?: false
    }

    override fun contains(id: Identifier): Boolean {
        return recipes.contains(id)
    }

    override fun remove(recipe: Recipe<*>) {
        remove(recipe.id)
    }

    override fun remove(id: Identifier) {
        recipes.remove(id)
        toBeDisplayed.remove(id)
    }

    override fun shouldDisplay(recipe: Recipe<*>): Boolean {
        return toBeDisplayed.contains(recipe.id)
    }

    override fun onRecipeDisplayed(recipe: Recipe<*>) {
        toBeDisplayed.remove(recipe.id)
    }

    override fun display(recipe: Recipe<*>) {
        display(recipe.id)
    }

    override fun display(id: Identifier) {
        toBeDisplayed.add(id)
    }

    override fun isGuiOpen(category: RecipeBookCategory): Boolean {
        return options.isGuiOpen(category)
    }

    override fun setGuiOpen(category: RecipeBookCategory, open: Boolean) {
        options.setGuiOpen(category, open)
    }

    override fun isFilteringCraftable(handler: AbstractRecipeScreenHandler<*>): Boolean {
        return isFilteringCraftable(handler.category)
    }

    override fun isFilteringCraftable(category: RecipeBookCategory): Boolean {
        return options.isFilteringCraftable(category)
    }

    override fun setFilteringCraftable(category: RecipeBookCategory, filteringCraftable: Boolean) {
        options.setFilteringCraftable(category, filteringCraftable)
    }

    override fun setOptions(options: RecipeBookOptions) {
        this.options.copyFrom(options)
    }

    override fun getOptions(): RecipeBookOptions {
        return options.copy()
    }

    override fun setCategoryOptions(category: RecipeBookCategory, guiOpen: Boolean, filteringCraftable: Boolean) {
        options.setGuiOpen(category, guiOpen)
        options.setFilteringCraftable(category, filteringCraftable)
    }
}
