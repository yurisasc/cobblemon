package com.cobblemon.mod.common.gui.cooking

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.gui.CookingPotScreenHandler
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget
import net.minecraft.client.gui.widget.TexturedButtonWidget
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.AbstractRecipeScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class CookingPotHandledScreen(
    handler: CookingPotScreenHandler,
    inventory: PlayerInventory,
    title: Text?
) : HandledScreen<CookingPotScreenHandler>(handler, inventory, title), CookBookProvider {

    private val recipeBook = CookBookWidget()
    private var narrow = false

    companion object {
        //private val TEXTURE = Identifier("textures/gui/container/crafting_table.png")
        val TEXTURE = cobblemonResource("textures/gui/cooking/cooking_pot.png")
        private val RECIPE_BUTTON_TEXTURE = Identifier("textures/gui/recipe_button.png")
        val TEXTURE_HEIGHT = 198
        val TEXTURE_WIDTH = 176
    }

    override fun init() {
        super.init()
        narrow = width < 379
        val recipeBookOffsetX = 12
        val recipeBookOffsetY = 40
        recipeBook.initialize(width, height, client, narrow, handler as AbstractRecipeScreenHandler<*>)
        x = recipeBook.findLeftEdge(width, TEXTURE_WIDTH)
        addDrawableChild(
            TexturedButtonWidget(x + recipeBookOffsetX, height / 2 - recipeBookOffsetY, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE) {
                recipeBook.toggleOpen()
                x = recipeBook.findLeftEdge(width, TEXTURE_WIDTH)
                it.setPosition(x + recipeBookOffsetX, height / 2 - recipeBookOffsetY)
            }
        )
        addSelectableChild(recipeBook)
        setInitialFocus(recipeBook)
        titleX = 39
        titleY = -10
        playerInventoryTitleX = 8
        playerInventoryTitleY = 88
    }

    override fun handledScreenTick() {
        super.handledScreenTick()
        recipeBook.update()
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(context)
        if (recipeBook.isOpen() && narrow) {
            drawBackground(context, delta, mouseX, mouseY)
            recipeBook.render(context, mouseX, mouseY, delta)
        } else {
            recipeBook.render(context, mouseX, mouseY, delta)
            super.render(context, mouseX, mouseY, delta)
            recipeBook.drawGhostSlots(context, x, y, true, delta)
        }
        drawMouseoverTooltip(context, mouseX, mouseY)
        recipeBook.drawTooltip(context, x, y, mouseX, mouseY)
    }

    override fun drawBackground(context: DrawContext, delta: Float, mouseX: Int, mouseY: Int) {
        val i = x
        val j = (height - TEXTURE_HEIGHT) / 2

        blitk(
            matrixStack = context.matrices,
            texture = TEXTURE,
            x = x, // horizontal placement of GUI
            y = (height - TEXTURE_HEIGHT) / 2, // vertical placement of GUI

            width = TEXTURE_WIDTH * 1.0, // scale of the GUI width
            height = TEXTURE_HEIGHT * 1.0 // scale of the GUI height
        )

        //context.drawTexture(TEXTURE, i, j, 0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT)
    }

    override fun isPointWithinBounds(x: Int, y: Int, width: Int, height: Int, pointX: Double, pointY: Double): Boolean {
        return (!narrow || !recipeBook.isOpen()) && super.isPointWithinBounds(x, y, width, height, pointX, pointY)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (recipeBook.mouseClicked(mouseX, mouseY, button)) {
            focused = recipeBook
            return true
        }
        return if (narrow && recipeBook.isOpen()) {
            true
        } else super.mouseClicked(mouseX, mouseY, button)
    }

    override fun isClickOutsideBounds(mouseX: Double, mouseY: Double, left: Int, top: Int, button: Int): Boolean {
        val bl = mouseX < left.toDouble() || mouseY < top.toDouble() || mouseX >= (left + backgroundWidth).toDouble() || mouseY >= (top + backgroundHeight).toDouble()
        return recipeBook.isClickOutsideBounds(mouseX, mouseY, x, y, backgroundWidth, backgroundHeight, button) && bl
    }

    override fun onMouseClick(slot: Slot?, slotId: Int, button: Int, actionType: SlotActionType?) {
        super.onMouseClick(slot, slotId, button, actionType)
        recipeBook.slotClicked(slot)
    }

    override fun refreshCookBook() {
        recipeBook.refresh()
    }

    override fun getCookBookWidget(): CookBookWidget {
        return recipeBook
    }
}
