package com.cablemc.pokemoncobbled.common.client.gui.startselection

import com.cablemc.pokemoncobbled.common.api.gui.ColourLibrary
import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.config.starter.StarterCategory
import com.cablemc.pokemoncobbled.common.util.asTranslated
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.cablemc.pokemoncobbled.common.api.gui.drawCenteredText
import com.cablemc.pokemoncobbled.common.client.CobbledResources
import com.cablemc.pokemoncobbled.common.client.gui.startselection.widgets.CategoryButton
import com.cablemc.pokemoncobbled.common.client.gui.startselection.widgets.ExitButton
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack

/**
 * Starterselection Screen Thingy
 *
 * @author Qu
 * @since 2022-06-18
 */
class StarterSelectionScreen private constructor(): Screen("pokemoncobbled.ui.starter.title".asTranslated()) {

    companion object {
        // Size of UI at scale 1
        private const val BASE_WIDTH = 200
        private const val BASE_HEIGHT = 150

        // Resources
        private val base = cobbledResource("ui/starterselection/starterselection_base.png")
        private val baseUnderlay = cobbledResource("ui/starterselection/starterselection_base_underlay.png")
        private val baseFrame = cobbledResource("ui/starterselection/starterselection_base_frame.png")
    }

    lateinit var categories: List<StarterCategory>

    constructor(categories: List<StarterCategory>) : this() {
        this.categories = categories
    }

    var selection = 0

    override fun init() {
        super.init()

        val x = (width - BASE_WIDTH) / 2
        val y = (height - BASE_HEIGHT) / 2

        categories.forEachIndexed { index, it ->
            addDrawableChild(
                categoryButton(
                    x = x + 5, y = y + if (index == 0) 5 else index * 22,
                    width = 57, height = 13,
                    index = index, category = it
                )
            )
        }

        addDrawableChild(
            ExitButton(
                pX = x + 176, pY = y + 2,
                pWidth = 21, pHeight = 15,
                pXTexStart = 0, pYTexStart = 0, pYDiffText = 0
            ) {
                MinecraftClient.getInstance().setScreen(null)
            }
        )
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        val x = (width - BASE_WIDTH) / 2
        val y = (height - BASE_HEIGHT) / 2
        // Render Underlay
        blitk(
            matrixStack = matrices,
            texture = baseUnderlay,
            x = x, y = y,
            width = BASE_WIDTH, height = BASE_HEIGHT
        )
        // Render Base
        blitk(
            matrixStack = matrices,
            texture = base,
            x = x, y = y,
            width = BASE_WIDTH, height = BASE_HEIGHT
        )
        // Render Frame
        blitk(
            matrixStack = matrices,
            texture = baseFrame,
            x = x, y = y,
            width = BASE_WIDTH, height = BASE_HEIGHT
        )
        // Render Text
        matrices.push()
        matrices.scale(0.75f, 0.95f, 0.95f)
        drawCenteredText(
            poseStack = matrices,
            font = CobbledResources.NOTO_SANS_BOLD,
            text = "pokemoncobbled.ui.starter.title".asTranslated(),
            x = (x + 122.5) / 0.75f, y = (y + 2.5f) / 0.95f,
            colour = ColourLibrary.WHITE,
            shadow = false
        )
        matrices.pop()
        // Render the rest
        super.render(matrices, mouseX, mouseY, delta)
    }

    private fun categoryButton(x: Int, y: Int, width: Int, height: Int, index: Int, category: StarterCategory) : CategoryButton {
        return CategoryButton(
            pX = x, pY = y,
            pWidth = width, pHeight = height,
            pXTexStart = 0, pYTexStart = 0, pYDiffText = 0,
            category = category
        ) {
            selection = index
        }
    }

    override fun shouldPause() = true
}