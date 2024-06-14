package com.cobblemon.mod.common.gui.cooking

import com.google.common.collect.Lists
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.screen.narration.NarrationPart
import net.minecraft.client.gui.screen.recipebook.RecipeBookResults
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection
import net.minecraft.client.gui.tooltip.Tooltip
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.book.RecipeBook
import net.minecraft.screen.AbstractRecipeScreenHandler
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper

@Environment(EnvType.CLIENT)
class CookBookAnimatedResultButton : ClickableWidget(0, 0, 25, 25, ScreenTexts.EMPTY) {
    val BACKGROUND_TEXTURE = Identifier("textures/gui/recipe_book.png")
    val field_32414 = 15.0f
    val field_32415 = 25
    val field_32413 = 30
    val MORE_RECIPES_TEXT = Text.translatable("gui.recipebook.moreRecipes")
    lateinit var craftingScreenHandler: AbstractRecipeScreenHandler<*>
    lateinit var recipeBook: RecipeBook
    lateinit var resultCollection: RecipeResultCollection
    var time = 0f
    var bounce = 0f
    var currentResultIndex = 0

    fun showResultCollection(resultCollection: RecipeResultCollection, results: CookBookResults) {
        this.resultCollection = resultCollection
        this.craftingScreenHandler = results.getClientValue().player?.currentScreenHandler as AbstractRecipeScreenHandler<*>
        this.recipeBook = results.getCookBookValue()
        val list = resultCollection.getResults(this.recipeBook.isFilteringCraftable(this.craftingScreenHandler))
        for (recipe in list) {
            if (this.recipeBook.shouldDisplay(recipe)) {
                results.onRecipesDisplayed(list)
                this.bounce = 15.0f
                break
            }
        }
    }

    fun getResultCollectionValue(): RecipeResultCollection {
        return this.resultCollection
    }

    override fun renderButton(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        if (!Screen.hasControlDown()) {
            this.time += delta
        }
        val minecraftClient = MinecraftClient.getInstance()
        var i = 29
        if (!this.resultCollection.hasCraftableRecipes()) {
            i += 25
        }
        var j = 206
        if (this.resultCollection.getResults(this.recipeBook.isFilteringCraftable(this.craftingScreenHandler)).size > 1) {
            j += 25
        }
        val bl = this.bounce > 0.0f
        if (bl) {
            val f = 1.0f + 0.1f * Math.sin((this.bounce / 15.0f * Math.PI).toDouble()).toFloat()
            context.matrices.push()
            context.matrices.translate((this.x + 8).toFloat(), (this.y + 12).toFloat(), 0.0f)
            context.matrices.scale(f, f, 1.0f)
            context.matrices.translate(-(this.x + 8).toFloat(), -(this.y + 12).toFloat(), 0.0f)
            this.bounce -= delta
        }
        context.drawTexture(BACKGROUND_TEXTURE, this.x, this.y, i, j, this.width, this.height)
        val list = this.results
        this.currentResultIndex = MathHelper.floor(this.time / 30.0f) % list.size
        val itemStack = list[this.currentResultIndex].getOutput(this.resultCollection.registryManager)
        var k = 4
        if (this.resultCollection.hasSingleOutput() && this.results.size > 1) {
            context.drawItem(itemStack, this.x + k + 1, this.y + k + 1, 0, 10)
            k--
        }
        context.drawItemWithoutEntity(itemStack, this.x + k, this.y + k)
        if (bl) {
            context.matrices.pop()
        }
    }

    private val results: List<Recipe<*>>
        get() {
            val list = this.resultCollection.getRecipes(true)
            if (!this.recipeBook.isFilteringCraftable(this.craftingScreenHandler)) {
                list.addAll(this.resultCollection.getRecipes(false))
            }
            return list
        }

    fun hasResults(): Boolean {
        return this.results.size == 1
    }

    fun currentRecipe(): Recipe<*> {
        return this.results[this.currentResultIndex]
    }

    fun getRecipeTooltip(): List<Text> {
        val itemStack = this.results[this.currentResultIndex].getOutput(this.resultCollection.registryManager)
        val list = Lists.newArrayList(Screen.getTooltipFromItem(MinecraftClient.getInstance(), itemStack))
        if (this.resultCollection.getResults(this.recipeBook.isFilteringCraftable(this.craftingScreenHandler)).size > 1) {
            list.add(MORE_RECIPES_TEXT)
        }
        return list
    }

    override fun appendClickableNarrations(builder: NarrationMessageBuilder) {
        val itemStack = this.results[this.currentResultIndex].getOutput(this.resultCollection.registryManager)
        builder.put(NarrationPart.TITLE, Text.translatable("narration.recipe", itemStack.name))
        if (this.resultCollection.getResults(this.recipeBook.isFilteringCraftable(this.craftingScreenHandler)).size > 1) {
            builder.put(NarrationPart.USAGE, Text.translatable("narration.button.usage.hovered"), Text.translatable("narration.recipe.usage.more"))
        } else {
            builder.put(NarrationPart.USAGE, Text.translatable("narration.button.usage.hovered"))
        }
    }

    override fun getWidth(): Int {
        return 25
    }

    override fun isValidClickButton(button: Int): Boolean {
        return button == 0 || button == 1
    }
}