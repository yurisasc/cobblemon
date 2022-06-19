package com.cablemc.pokemoncobbled.common.client.gui.battle.widgets

import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.api.moves.Moves
import com.cablemc.pokemoncobbled.common.battles.InBattleMove
import com.cablemc.pokemoncobbled.common.client.CobbledResources
import com.cablemc.pokemoncobbled.common.client.battle.ActiveClientBattlePokemon
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.type.TypeWidget
import com.cablemc.pokemoncobbled.common.client.render.drawScaledText
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.cablemc.pokemoncobbled.common.util.lang
import com.cablemc.pokemoncobbled.common.util.math.toRGB
import net.minecraft.client.gui.Drawable
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.Selectable
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.screen.narration.NarrationPart
import net.minecraft.client.util.math.MatrixStack

class BattleMoveTile(
    val activeBattlePokemon: ActiveClientBattlePokemon,
    val move: InBattleMove,
    val x: Float,
    val y: Float,
    val onClick: () -> Unit
) : Drawable, Element, Selectable {
    val moveDef = Moves.getByNameOrDummy(move.id)
    val moveName = lang("move.${move.id}")

    companion object {
        const val MOVE_TILE_WIDTH_TO_HEIGHT = 382 / 84F
        const val MOVE_TILE_WIDTH = 100F
        const val MOVE_TILE_HEIGHT = MOVE_TILE_WIDTH / MOVE_TILE_WIDTH_TO_HEIGHT
        val moveResource = cobbledResource("ui/battle/battle_move.png")
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        val (r, g, b) = moveDef.elementalType.hue.toRGB()

        blitk(
            matrixStack = matrices,
            texture = moveResource,
            x = x,
            y = y,
            width = MOVE_TILE_WIDTH,
            height = MOVE_TILE_HEIGHT,
            red = r,
            green = g,
            blue = b
        )

        drawScaledText(
            matrixStack = matrices,
            font = CobbledResources.NOTO_SANS_BOLD_SMALL,
            text = moveName,
            x = x + 20,
            y = y + 5
        )

        blitk(
            matrixStack = matrices,
            texture = TypeWidget.typeResource,
            x = x,
            y = y,
            width = MOVE_TILE_HEIGHT, height = MOVE_TILE_WIDTH,
            uOffset = MOVE_TILE_WIDTH * moveDef.elementalType.textureXMultiplier.toFloat() + 0.1,
            textureWidth = MOVE_TILE_WIDTH * 18
        )
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (!isHovered(mouseX, mouseY)) {
            return false
        }

        onClick()
        return true
    }

    fun isHovered(mouseX: Double, mouseY: Double) = mouseX > x && mouseY > y && mouseX < x + MOVE_TILE_WIDTH && mouseY < y + MOVE_TILE_HEIGHT

    override fun appendNarrations(builder: NarrationMessageBuilder) = builder.put(NarrationPart.TITLE, moveName)
    override fun getType() = Selectable.SelectionType.HOVERED

}