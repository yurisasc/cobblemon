package com.cablemc.pokemoncobbled.common.client.gui.summary

import com.cablemc.pokemoncobbled.common.api.gui.ColourLibrary
import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.client.CobbledResources
import com.cablemc.pokemoncobbled.common.client.render.drawScaledText
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.client.gui.widget.TexturedButtonWidget
import net.minecraft.client.sound.SoundManager
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class SummaryButton(
    var buttonX: Float,
    var buttonY: Float,
    val buttonWidth: Number,
    val buttonHeight: Number,
    clickAction: PressAction,
    private val text: MutableText,
    private val resource: Identifier = cobbledResource("ui/summary/summary_button.png"),
    private val renderRequirement: ((button: TexturedButtonWidget) -> Boolean) = { true },
    private val clickRequirement: ((button: TexturedButtonWidget) -> Boolean) = { true },
    private val hoverColorRequirement: ((button: TexturedButtonWidget) -> Boolean) = { button -> button.isHovered },
    private val silent: Boolean = false,
    private val buttonScale: Float = 1F,
    private val textScale: Float = 1F
): TexturedButtonWidget(buttonX.toInt(), buttonY.toInt(), buttonWidth.toInt(), buttonHeight.toInt(), 0, 0, 0, resource, buttonWidth.toInt(), buttonHeight.toInt(), clickAction) {

    override fun mouseDragged(d: Double, e: Double, i: Int, f: Double, g: Double) = false

    override fun renderButton(poseStack: MatrixStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        if (!this.renderRequirement.invoke(this)) {
            return
        }
        // Render Button Image
        blitk(
            matrixStack = poseStack,
            texture = this.resource,
            x = buttonX, y = buttonY,
            width = buttonWidth, height = buttonHeight
        )
        // Draw Text
        drawScaledText(
            matrixStack = poseStack,
            font = null,
            text = this.text,
            scale = textScale,
            x = this.buttonX + buttonWidth.toFloat() / 2,
            y = buttonY + 0.2F * buttonHeight.toFloat(),
            colour = if (this.hoverColorRequirement.invoke(this)) ColourLibrary.BUTTON_HOVER_COLOUR else ColourLibrary.WHITE,
            shadow = false,
            maxCharacterWidth = (buttonWidth.toFloat() * 0.8F).toInt(),
            centered = true
        )
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (this.clickRequirement.invoke(this)) {
            return super.mouseClicked(mouseX, mouseY, button)
        }
        return false
    }

    override fun playDownSound(soundManager: SoundManager?) {
        if (!this.silent) {
            super.playDownSound(soundManager)
        }
    }

    fun setPosFloat(x: Float, y: Float) {
        setPos(x.toInt(), y.toInt())
        this.buttonX = x
        this.buttonY = y
    }

    companion object {
        const val BUTTON_WIDTH = 28
        const val BUTTON_HEIGHT = 14
    }
}