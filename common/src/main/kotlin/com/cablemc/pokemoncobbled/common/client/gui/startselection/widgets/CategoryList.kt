package com.cablemc.pokemoncobbled.common.client.gui.startselection.widgets

import com.cablemc.pokemoncobbled.common.api.gui.ColourLibrary
import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.api.gui.drawCenteredText
import com.cablemc.pokemoncobbled.common.api.text.hover
import com.cablemc.pokemoncobbled.common.client.CobbledResources
import com.cablemc.pokemoncobbled.common.client.gui.startselection.StarterSelectionScreen
import com.cablemc.pokemoncobbled.common.client.render.drawScaledText
import com.cablemc.pokemoncobbled.common.config.starter.StarterCategory
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text

class CategoryList(
    private val paneWidth: Int,
    private val paneHeight: Int,
    topOffset: Int,
    bottomOffset: Int,
    private val entryWidth: Int,
    entryHeight: Int,
    private val categories: List<StarterCategory>,
    val x: Int, val y: Int,
    private val minecraft: MinecraftClient = MinecraftClient.getInstance(),
    private val starterSelectionScreen: StarterSelectionScreen
) : AlwaysSelectedEntryListWidget<CategoryList.Category>(
    minecraft,
    paneWidth,
    paneHeight,
    topOffset,
    bottomOffset,
    entryHeight
) {

    companion object {
        private const val CATEGORY_BUTTON_WIDTH = 51.5f
        private const val CATEGORY_BUTTON_HEIGHT = 16f
        private const val ENTRY_X_OFFSET = 10f
        private val categoryResource = cobbledResource("ui/starterselection/starterselection_slot.png")
    }

    init {
        this.correctSize()
        this.setRenderHorizontalShadows(false)
        this.setRenderBackground(false)
        this.setRenderSelection(false)
    }

    private var entriesCreated = false

    private fun createEntries() = categories.map {
        Category(it)
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        if (!entriesCreated) {
            createEntries().forEach { addEntry(it) }
            entriesCreated = true
        }
        RenderSystem.enableScissor(
            (x * minecraft.window.scaleFactor).toInt(),
            (minecraft.window.height - (y * minecraft.window.scaleFactor) - (height * minecraft.window.scaleFactor)).toInt(),
            (width * minecraft.window.scaleFactor).toInt(),
            (height * minecraft.window.scaleFactor).toInt()
        )
        super.render(matrices, mouseX, mouseY, delta)
        RenderSystem.disableScissor()
    }

    private fun correctSize() {
        this.updateSize(this.paneWidth, this.paneHeight, this.y, this.y + this.paneHeight)
        this.setLeftPos(this.x)
    }

    private fun scale(n: Int): Int = (this.client.window.scaleFactor * n).toInt()
    override fun getRowWidth() = this.entryWidth
    override fun getScrollbarPositionX(): Int {
        return this.left + this.width - 5
    }


    inner class Category(private val category: StarterCategory) : AlwaysSelectedEntryListWidget.Entry<Category>() {

        override fun render(
            matrices: MatrixStack,
            index: Int,
            y: Int,
            x: Int,
            entryWidth: Int,
            entryHeight: Int,
            mouseX: Int,
            mouseY: Int,
            hovered: Boolean,
            tickDelta: Float
        ) {
            val isHovered = mouseX >= x && mouseY >= y && mouseX < x + entryWidth && mouseY < y + (entryHeight - 1)
            if (isHovered) {
                blitk(
                    matrixStack = matrices,
                    x = x + 0.5f, y = y,
                    texture = categoryResource,
                    width = CATEGORY_BUTTON_WIDTH, height = CATEGORY_BUTTON_HEIGHT,
                    red = 0.75f, green = 0.75f, blue = 0.75f
                )
            } else
                blitk(
                    matrixStack = matrices,
                    x = x + 0.5f, y = y,
                    texture = categoryResource,
                    width = CATEGORY_BUTTON_WIDTH, height = CATEGORY_BUTTON_HEIGHT
                )
            drawScaledText(
                matrixStack = matrices,
                text = category.displayName,
                scale = 0.95F,
                x = x + 26,
                y = y + 4F,
                maxCharacterWidth = 50,
                shadow = false,
                centered = true
            )
        }

        override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
            starterSelectionScreen.changeCategory(category = category)
            return true
        }

        override fun getNarration(): Text {
            return Text.of("Yes")
        }
    }
}