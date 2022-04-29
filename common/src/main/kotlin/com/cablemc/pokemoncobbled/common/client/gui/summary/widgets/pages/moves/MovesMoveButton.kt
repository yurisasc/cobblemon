package com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.pages.moves

import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.mojang.blaze3d.vertex.MatrixStack
import com.mojang.math.Vec3f
import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.LiteralText

/**
 * This Button is specifically made for the Summary to change the order of the Moves
 *
 * The blocked var was added to prevent the switching the order of Buttons triggering another switch
 */
class MovesMoveButton(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    private val isUp: Boolean,
    onPress: OnPress
): Button(pX, pY, pWidth, pHeight, LiteralText("MoveButton"), onPress) {

    companion object {
        private const val MOVE_BUTTON_WIDTH = 11
        private const val MOVE_BUTTON_HEIGHT = 8.2F
        private val buttonResource = cobbledResource("ui/summary/summary_moves_overlay_swap_up.png")
        private var blocked = false
    }

    override fun mouseDragged(d: Double, e: Double, i: Int, f: Double, g: Double): Boolean {
        return false
    }

    override fun renderButton(pMatrixStack: MatrixStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        if (isHovered) {
            if (isUp) {
                blitk(
                    poseStack = pMatrixStack,
                    x = x + 1.5F, y = y + 1.8F,
                    texture = buttonResource,
                    width = MOVE_BUTTON_WIDTH, height = MOVE_BUTTON_HEIGHT
                )
            } else {
                pMatrixStack.push()
                pMatrixStack.mulPose(Vec3f.ZP.rotationDegrees(180.0f))
                blitk(
                    poseStack = pMatrixStack,
                    x = (x + 12.5F) * -1, y = (y + 0.9F + MOVE_BUTTON_HEIGHT) * -1,
                    texture = buttonResource,
                    width = MOVE_BUTTON_WIDTH, height = MOVE_BUTTON_HEIGHT
                )
                pMatrixStack.pop()
            }
        }
    }

    override fun onRelease(pMouseX: Double, pMouseY: Double) {
        blocked = false
    }

    override fun onClick(pMouseX: Double, pMouseY: Double) {
        if (!blocked) {
            blocked = true
            onPress.onPress(this)
        }
    }
}