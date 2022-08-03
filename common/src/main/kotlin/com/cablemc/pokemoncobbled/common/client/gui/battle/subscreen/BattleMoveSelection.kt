package com.cablemc.pokemoncobbled.common.client.gui.battle.subscreen

import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.api.moves.Moves
import com.cablemc.pokemoncobbled.common.battles.InBattleMove
import com.cablemc.pokemoncobbled.common.battles.MoveActionResponse
import com.cablemc.pokemoncobbled.common.client.battle.SingleActionRequest
import com.cablemc.pokemoncobbled.common.client.gui.battle.BattleGUI
import com.cablemc.pokemoncobbled.common.client.render.drawScaledText
import com.cablemc.pokemoncobbled.common.util.battleLang
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.cablemc.pokemoncobbled.common.util.math.toRGB
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack

class BattleMoveSelection(
    battleGUI: BattleGUI,
    request: SingleActionRequest,
) : BattleActionSelection(
    battleGUI = battleGUI,
    request = request,
    x = 10,
    y = MinecraftClient.getInstance().window.scaledHeight - 100,
    width = 100,
    height = 100,
    battleLang("ui.select_move")
) {
    companion object {
        const val MOVE_WIDTH_TO_HEIGHT = 352 / 84F
        const val MOVE_WIDTH = 100F
        const val MOVE_HEIGHT = MOVE_WIDTH / MOVE_WIDTH_TO_HEIGHT
        const val MOVE_VERTICAL_SPACING = 7F
        const val MOVE_HORIZONTAL_SPACING = 7F
        val moveTexture = cobbledResource("ui/battle/battle_move.png")
    }

    val moveSet = request.moveSet!!
    val moveTiles = moveSet.moves.mapIndexed { index, inBattleMove ->
        val isEven = index % 2 == 0
        val x = (if (isEven) this.x.toFloat() else this.x + MOVE_HORIZONTAL_SPACING + MOVE_WIDTH) + if (index > 1) 8 else 0
        val y = if (index > 1) this.y + MOVE_HEIGHT + MOVE_VERTICAL_SPACING else this.y.toFloat()
        MoveTile(this, inBattleMove, x, y)
    }

    class MoveTile(
        val moveSelection: BattleMoveSelection,
        val move: InBattleMove,
        val x: Float,
        val y: Float
    ) {
        val moveTemplate = Moves.getByNameOrDummy(move.id)
        val rgb = moveTemplate.elementalType.hue.toRGB()

        fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
            blitk(
                matrixStack = matrices,
                texture = moveTexture,
                x = x,
                y = y,
                width = MOVE_WIDTH,
                height = MOVE_HEIGHT,
                red = rgb.first,
                green = rgb.second,
                blue = rgb.third,
                alpha = moveSelection.opacity
            )

            blitk(
                matrixStack = matrices,
                texture = moveTemplate.elementalType.resourceLocation,
                x = x + 0.5,
                y = y + 0.5,
                height = MOVE_HEIGHT - 1,
                width = MOVE_HEIGHT - 1.5,
                uOffset = (MOVE_HEIGHT - 1.5) * moveTemplate.elementalType.textureXMultiplier.toFloat() + 0.1,
                textureWidth = (MOVE_HEIGHT - 1.5) * 18,
                alpha = moveSelection.opacity
            )

            drawScaledText(
                matrixStack = matrices,
                text = moveTemplate.displayName,
                x = x + 28,
                y = y + 5,
                scale = 0.7F,
                opacity = moveSelection.opacity
            )
        }

        fun isHovered(mouseX: Double, mouseY: Double) = mouseX >= x && mouseX <= x + MOVE_WIDTH && mouseY >= y && mouseY <= y + MOVE_HEIGHT

        fun onClick() {
            moveSelection.playDownSound(MinecraftClient.getInstance().soundManager)
            val targets = move.target.targetList(moveSelection.request.activePokemon)
            if (targets == null) {
                moveSelection.battleGUI.selectAction(moveSelection.request, MoveActionResponse(move.id, null))
            } else if (targets.size == 1) {
                moveSelection.battleGUI.selectAction(moveSelection.request, MoveActionResponse(move.id, targets[0].getPNX()))
            } else {
                // Target selection
            }
        }
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        moveTiles.forEach {
            it.render(matrices, mouseX, mouseY, delta)
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val move = moveTiles.find { it.isHovered(mouseX, mouseY) }
        if (move != null) {
            move.onClick()
            return true
        }
        return false
    }
}