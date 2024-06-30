package com.cobblemon.mod.common.client.gui.tm

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.types.ElementalType
import com.cobblemon.mod.common.client.gui.TypeIcon
import com.cobblemon.mod.common.client.gui.TypeReturnIcon
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.sound.SoundInstance
import net.minecraft.client.sound.SoundManager

class TypeButton(
    pX: Int, pY: Int,
    onPress: PressAction,
    val type: ElementalType?
): ButtonWidget(pX, pY, WIDTH.toInt(), HEIGHT.toInt(), type?.displayName, onPress, DEFAULT_NARRATION_SUPPLIER) {

    companion object {
        private const val WIDTH = 24F
        private const val HEIGHT = 24F
        val TYPE_BUTTON = cobblemonResource("textures/gui/tm/type_button.png")
    }

    override fun renderWidget(context: DrawContext, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        blitk(
            matrixStack = context.matrices,
            texture = TYPE_BUTTON,
            x = x,
            y = y,
            width = WIDTH,
            height = HEIGHT,
            vOffset = if (isHovered(pMouseX.toDouble(), pMouseY.toDouble())) HEIGHT else 0,
            textureHeight = HEIGHT * 2
        )

        context.matrices.push()
        context.matrices.scale(2f, 2f, 2f)

        if (type != null) {
            TypeIcon(
                    x = (x + 12.125) / 2,
                    y = (y + 3) / 2,
                    type = type,
                    centeredX = true,
                    small = true
            ).render(context)
        }
        else {
            TypeReturnIcon(
                    x = (x + 12.125) / 2,
                    y = (y + 3) / 2,
                    centeredX = true,
                    small = true
            ).render(context)
        }


        context.matrices.pop()
    }

    override fun playDownSound(soundManager: SoundManager) {

    }
    fun isHovered(mouseX: Double, mouseY: Double) = mouseX.toFloat() in (x.toFloat()..(x.toFloat() + WIDTH)) && mouseY.toFloat() in (y.toFloat()..(y.toFloat() + HEIGHT))


}