package com.cobblemon.mod.common.gui.cooking

import com.google.common.collect.ImmutableList
import com.google.common.collect.Lists
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.recipebook.AnimatedResultButton
import net.minecraft.client.gui.screen.recipebook.RecipeAlternativesWidget
import net.minecraft.client.gui.screen.recipebook.RecipeDisplayListener
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.gui.widget.ToggleButtonWidget
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.book.RecipeBook
import org.jetbrains.annotations.Nullable
import java.util.function.Consumer

@Environment(EnvType.CLIENT)
class CookBookResults {
    val resultButtons = Lists.newArrayListWithCapacity<CookBookAnimatedResultButton>(20)
    @Nullable
    var hoveredResultButton: CookBookAnimatedResultButton? = null
    val alternatesWidget = RecipeAlternativesWidget()
    lateinit var client: MinecraftClient
    val recipeDisplayListeners = Lists.newArrayList<RecipeDisplayListener>()
    var resultCollections = ImmutableList.of<RecipeResultCollection>()
    lateinit var nextPageButton: ToggleButtonWidget
    lateinit var prevPageButton: ToggleButtonWidget
    var pageCount = 0
    var currentPage = 0
    lateinit var recipeBook: RecipeBook
    @Nullable
    var lastClickedRecipe: Recipe<*>? = null
    @Nullable
    var resultCollection: RecipeResultCollection? = null

    init {
        for (i in 0 until 20) {
            resultButtons.add(CookBookAnimatedResultButton())
            /*resultButtons.add(CookBookAnimatedResultButton().apply {
                // Initialize resultCollection with a default or dummy value if possible
                this.resultCollection = RecipeResultCollection()
            })*/
        }
    }

    fun initialize(client: MinecraftClient, parentLeft: Int, parentTop: Int) {
        this.client = client
        this.recipeBook = client.player?.recipeBook ?: return

        for (i in resultButtons.indices) {
            resultButtons[i].setPosition(parentLeft + 11 + 25 * (i % 5), parentTop + 31 + 25 * (i / 5))
        }

        nextPageButton = ToggleButtonWidget(parentLeft + 93, parentTop + 137, 12, 17, false)
        nextPageButton.setTextureUV(1, 208, 13, 18, CookBookWidget.TEXTURE)
        prevPageButton = ToggleButtonWidget(parentLeft + 38, parentTop + 137, 12, 17, true)
        prevPageButton.setTextureUV(1, 208, 13, 18, CookBookWidget.TEXTURE)
    }

    fun setGui(widget: CookBookWidget) {
        recipeDisplayListeners.remove(widget)
        recipeDisplayListeners.add(widget)
    }

    fun setResults(resultCollections: List<RecipeResultCollection>, resetCurrentPage: Boolean) {
        this.resultCollections = ImmutableList.copyOf(resultCollections)
        this.pageCount = Math.ceil(resultCollections.size / 20.0).toInt()
        if (this.pageCount <= this.currentPage || resetCurrentPage) {
            this.currentPage = 0
        }
        refreshResultButtons()
    }

    fun refreshResultButtons() {
        val i = 20 * this.currentPage
        for (j in resultButtons.indices) {
            val animatedResultButton = resultButtons[j]
            if (i + j < resultCollections.size) {
                val recipeResultCollection = resultCollections[i + j]
                animatedResultButton.showResultCollection(recipeResultCollection, this)
                animatedResultButton.visible = true
            } else {
                animatedResultButton.visible = false
            }
        }
        hideShowPageButtons()
    }

    fun hideShowPageButtons() {
        nextPageButton.visible = pageCount > 1 && currentPage < pageCount - 1
        prevPageButton.visible = pageCount > 1 && currentPage > 0
    }

    fun draw(context: DrawContext, x: Int, y: Int, mouseX: Int, mouseY: Int, delta: Float) {
        if (pageCount > 1) {
            val string = "${currentPage + 1}/$pageCount"
            val i = client.textRenderer.getWidth(string)
            context.drawText(client.textRenderer, string, x - i / 2 + 73, y + 141, -1, false)
        }
        hoveredResultButton = null
        for (animatedResultButton in resultButtons) {
            animatedResultButton.render(context, mouseX, mouseY, delta)
            if (animatedResultButton.visible && animatedResultButton.isSelected()) {
                hoveredResultButton = animatedResultButton
            }
        }
        prevPageButton.render(context, mouseX, mouseY, delta)
        nextPageButton.render(context, mouseX, mouseY, delta)
        alternatesWidget.render(context, mouseX, mouseY, delta)
    }

    fun drawTooltip(context: DrawContext, x: Int, y: Int) {
        if (client.currentScreen != null && hoveredResultButton != null && !alternatesWidget.isVisible()) {
            context.drawTooltip(client.textRenderer, hoveredResultButton!!.getRecipeTooltip(), x, y)
        }
    }

    @Nullable
    fun getLastClickedRecipeValue(): Recipe<*>? {
        return lastClickedRecipe
    }

    @Nullable
    fun getLastClickedResults(): RecipeResultCollection? {
        return resultCollection
    }

    fun hideAlternates() {
        alternatesWidget.isVisible = false
    }

    fun mouseClicked(mouseX: Double, mouseY: Double, button: Int, areaLeft: Int, areaTop: Int, areaWidth: Int, areaHeight: Int): Boolean {
        lastClickedRecipe = null
        resultCollection = null
        if (alternatesWidget.isVisible()) {
            if (alternatesWidget.mouseClicked(mouseX, mouseY, button)) {
                lastClickedRecipe = alternatesWidget.getLastClickedRecipe()
                resultCollection = alternatesWidget.getResults()
            } else {
                alternatesWidget.isVisible = false
            }
            return true
        } else if (nextPageButton.mouseClicked(mouseX, mouseY, button)) {
            currentPage++
            refreshResultButtons()
            return true
        } else if (prevPageButton.mouseClicked(mouseX, mouseY, button)) {
            currentPage--
            refreshResultButtons()
            return true
        } else {
            for (animatedResultButton in resultButtons) {
                if (animatedResultButton.mouseClicked(mouseX, mouseY, button)) {
                    if (button == 0) {
                        lastClickedRecipe = animatedResultButton.currentRecipe()
                        resultCollection = animatedResultButton.getResultCollectionValue()
                    } else if (button == 1 && !alternatesWidget.isVisible() && !animatedResultButton.hasResults()) {
                        alternatesWidget.showAlternativesForResult(client, animatedResultButton.getResultCollectionValue(), animatedResultButton.x, animatedResultButton.y, areaLeft + areaWidth / 2, areaTop + 13 + areaHeight / 2, animatedResultButton.width.toFloat())
                    }
                    return true
                }
            }
        }
        return false
    }

    fun onRecipesDisplayed(recipes: List<Recipe<*>>) {
        for (recipeDisplayListener in recipeDisplayListeners) {
            recipeDisplayListener.onRecipesDisplayed(recipes)
        }
    }

    fun getClientValue(): MinecraftClient {
        return client
    }

    fun getCookBookValue(): RecipeBook {
        return recipeBook
    }

    fun forEachButton(consumer: Consumer<ClickableWidget>) {
        consumer.accept(nextPageButton)
        consumer.accept(prevPageButton)
        resultButtons.forEach(consumer)
    }

    companion object {
        const val field_32411 = 20
    }
}
