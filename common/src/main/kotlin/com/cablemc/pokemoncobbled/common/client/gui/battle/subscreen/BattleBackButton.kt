package com.cablemc.pokemoncobbled.common.client.gui.battle.subscreen

import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.client.util.math.MatrixStack

class BattleBackButton(val x: Float, val y: Float) {
    companion object {
        const val WIDTH = 24F
        const val HEIGHT = WIDTH * 3/4F
    }

    fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        blitk(
            matrixStack = matrices,
            texture = cobbledResource("ui/pc/pc_exit.png"),
            x = x,
            y = y,
            height = HEIGHT,
            width = WIDTH
        )
    }

    fun isHovered(mouseX: Double, mouseY: Double) = mouseX.toFloat() in (x..(x + WIDTH)) && mouseY.toFloat() in (y..(y + HEIGHT))
}