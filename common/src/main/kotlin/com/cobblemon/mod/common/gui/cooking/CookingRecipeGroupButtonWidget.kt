package com.cobblemon.mod.common.gui.cooking

import com.mojang.blaze3d.systems.RenderSystem
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget
import net.minecraft.client.gui.widget.ToggleButtonWidget
import net.minecraft.client.recipebook.ClientRecipeBook
import net.minecraft.client.recipebook.RecipeBookGroup
import net.minecraft.client.render.item.ItemRenderer
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Recipe
import net.minecraft.screen.AbstractRecipeScreenHandler

@Environment(EnvType.CLIENT)
class CookingRecipeGroupButtonWidget(val category: RecipeBookGroup) : ToggleButtonWidget(0, 0, 35, 27, false) {
    private var bounce = 0f

    init {
        setTextureUV(153, 2, 35, 0, CookBookWidget.TEXTURE)
    }

    fun checkForNewRecipes(client: MinecraftClient) {
        val clientRecipeBook = client.player?.recipeBook ?: return
        val list = clientRecipeBook.getResultsForGroup(this.category)
        if (client.player?.currentScreenHandler is AbstractRecipeScreenHandler<*>) {
            for (recipeResultCollection in list) {
                for (recipe in recipeResultCollection.getResults(clientRecipeBook.isFilteringCraftable(client.player!!.currentScreenHandler as AbstractRecipeScreenHandler<*>))) {
                    if (clientRecipeBook.shouldDisplay(recipe)) {
                        this.bounce = 15.0f
                        return
                    }
                }
            }
        }
    }

    override fun renderButton(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        if (this.bounce > 0.0f) {
            val f = 1.0f + 0.1f * Math.sin((this.bounce / 15.0f * Math.PI).toDouble()).toFloat()
            context.matrices.push()
            context.matrices.translate((this.x + 8).toFloat(), (this.y + 12).toFloat(), 0.0f)
            context.matrices.scale(1.0f, f, 1.0f)
            context.matrices.translate(-(this.x + 8).toFloat(), -(this.y + 12).toFloat(), 0.0f)
        }

        val minecraftClient = MinecraftClient.getInstance()
        RenderSystem.disableDepthTest()
        var i = this.u
        var j = this.v
        if (this.toggled) {
            i += this.pressedUOffset
        }
        if (this.isSelected) {
            j += this.hoverVOffset
        }

        var k = this.x
        if (this.toggled) {
            k -= 2
        }

        context.drawTexture(this.texture, k, this.y, i, j, this.width, this.height)
        RenderSystem.enableDepthTest()
        this.renderIcons(context, minecraftClient.itemRenderer)
        if (this.bounce > 0.0f) {
            context.matrices.pop()
            this.bounce -= delta
        }
    }

    private fun renderIcons(context: DrawContext, itemRenderer: ItemRenderer) {
        val list = this.category.getIcons()
        val i = if (this.toggled) -2 else 0
        if (list.size == 1) {
            context.drawItemWithoutEntity(list[0], this.x + 9 + i, this.y + 5)
        } else if (list.size == 2) {
            context.drawItemWithoutEntity(list[0], this.x + 3 + i, this.y + 5)
            context.drawItemWithoutEntity(list[1], this.x + 14 + i, this.y + 5)
        }
    }

    fun getCategoryValue(): RecipeBookGroup {
        return this.category
    }

    fun hasKnownRecipes(recipeBook: ClientRecipeBook): Boolean {
        val list = recipeBook.getResultsForGroup(this.category)
        this.visible = false
        if (list != null) {
            for (recipeResultCollection in list) {
                if (recipeResultCollection.isInitialized && recipeResultCollection.hasFittingRecipes()) {
                    this.visible = true
                    break
                }
            }
        }
        return this.visible
    }
}