package com.cablemc.pokemoncobbled.common.client.gui.battle.widgets

import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.gui.battle.BattleGUI
import com.cablemc.pokemoncobbled.common.client.render.drawScaledText
import net.minecraft.client.gui.Drawable
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.Selectable
import net.minecraft.client.gui.Selectable.SelectionType.HOVERED
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.screen.narration.NarrationPart
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.MutableText
import net.minecraft.util.Identifier

class BattleOptionTile(
    val battleGUI: BattleGUI,
    val x: Int,
    val y: Int,
    val resource: Identifier,
    val text: MutableText,
    val onClick: () -> Unit
) : Drawable, Element, Selectable {
    companion object {
        const val OPTION_WIDTH_TO_HEIGHT = 352/80F
        const val  OPTION_WIDTH = 100F
        const val OPTION_HEIGHT = OPTION_WIDTH / OPTION_WIDTH_TO_HEIGHT
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        val opacity = PokemonCobbledClient.battleOverlay.opacityRatio
        if (opacity < 0.1) {
            return
        }
        blitk(
            matrixStack = matrices,
            x = x,
            y = y,
            alpha = opacity,
            width = OPTION_WIDTH,
            height = OPTION_HEIGHT,
            texture = resource
        )

        val scale = 1F
        drawScaledText(
            matrixStack = matrices,
            text = text,
            x = x + 24,
            y = y + 7,
            opacity = opacity,
            scale = scale
        )
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (mouseX < x || mouseY < y || mouseX > x + OPTION_WIDTH || mouseY > y + OPTION_HEIGHT) {
            return false
        }
        onClick()
        return true
    }

    override fun appendNarrations(builder: NarrationMessageBuilder) = builder.put(NarrationPart.TITLE, text)
    override fun getType() = HOVERED
}