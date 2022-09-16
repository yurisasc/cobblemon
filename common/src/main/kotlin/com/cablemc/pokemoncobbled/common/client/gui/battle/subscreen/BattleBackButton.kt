package com.cablemc.pokemoncobbled.common.client.gui.battle.subscreen

import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.client.util.math.MatrixStack

class BattleBackButton(val x: Float, val y: Float) {
    companion object {
        const val WIDTH = 58
        const val HEIGHT = 34
        const val SCALE = 0.5F
    }

    fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        blitk(
            matrixStack = matrices,
            texture = cobbledResource("ui/battle/battle_back.png"),
            x = x * 2,
            y = y * 2,
            height = HEIGHT,
            width = WIDTH,
            vOffset = if (isHovered(mouseX.toDouble(), mouseY.toDouble())) HEIGHT else 0,
            textureHeight = HEIGHT * 2,
            scale = SCALE
        )
    }

    fun isHovered(mouseX: Double, mouseY: Double) = mouseX.toFloat() in (x..(x + (WIDTH * SCALE))) && mouseY.toFloat() in (y..(y + (HEIGHT * SCALE)))
}