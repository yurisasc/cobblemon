package com.cablemc.pokemoncobbled.common.client.gui.battle.subscreen

import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.api.moves.Moves
import com.cablemc.pokemoncobbled.common.api.text.bold
import com.cablemc.pokemoncobbled.common.api.text.text
import com.cablemc.pokemoncobbled.common.battles.InBattleMove
import com.cablemc.pokemoncobbled.common.battles.MoveActionResponse
import com.cablemc.pokemoncobbled.common.client.CobbledResources
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
    x = 20,
    y = MinecraftClient.getInstance().window.scaledHeight - 84,
    width = 100,
    height = 100,
    battleLang("ui.select_move")
) {
    companion object {
        const val MOVE_WIDTH = 92
        const val MOVE_HEIGHT = 24
        const val MOVE_VERTICAL_SPACING = 5F
        const val MOVE_HORIZONTAL_SPACING = 13F
        const val TYPE_ICON_DIAMETER = 36
        val moveTexture = cobbledResource("ui/battle/battle_move.png")
        val moveOverlayTexture = cobbledResource("ui/battle/battle_move_overlay.png")
    }

    val moveSet = request.moveSet!!
    val moveTiles = moveSet.moves.mapIndexed { index, inBattleMove ->
        val isEven = index % 2 == 0
        val x = if (isEven) this.x.toFloat() else this.x + MOVE_HORIZONTAL_SPACING + MOVE_WIDTH
        val y = if (index > 1) this.y + MOVE_HEIGHT + MOVE_VERTICAL_SPACING else this.y.toFloat()
        MoveTile(this, inBattleMove, x, y)
    }

    val backButton = BattleBackButton(x.toFloat() + MOVE_HORIZONTAL_SPACING + MOVE_WIDTH + MOVE_WIDTH + 3F, y + 3F)

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
                vOffset = if (isHovered(mouseX.toDouble(), mouseY.toDouble())) MOVE_HEIGHT else 0,
                textureHeight = MOVE_HEIGHT * 2,
                red = rgb.first,
                green = rgb.second,
                blue = rgb.third,
                alpha = moveSelection.opacity
            )

            blitk(
                matrixStack = matrices,
                texture = moveOverlayTexture,
                x = x,
                y = y,
                width = MOVE_WIDTH,
                height = MOVE_HEIGHT,
                alpha = moveSelection.opacity
            )

            blitk(
                matrixStack = matrices,
                texture = moveTemplate.elementalType.resourceLocation,
                x = (x * 2) - (TYPE_ICON_DIAMETER / 2),
                y = (y + 2) * 2,
                height = TYPE_ICON_DIAMETER,
                width = TYPE_ICON_DIAMETER,
                uOffset = TYPE_ICON_DIAMETER * moveTemplate.elementalType.textureXMultiplier.toFloat() + 0.1,
                textureWidth = TYPE_ICON_DIAMETER * 18,
                alpha = moveSelection.opacity,
                scale = 0.5F
            )

            val categoryWidth = 24
            val categoryHeight = 16
            blitk(
                matrixStack = matrices,
                texture = moveTemplate.damageCategory.resourceLocation,
                x = (x + 48) * 2,
                y = (y + 14.5) * 2,
                width = categoryWidth,
                height = categoryHeight,
                vOffset = categoryHeight * moveTemplate.damageCategory.textureXMultiplier,
                textureHeight = categoryHeight * 3,
                scale = 0.5F
            )

            drawScaledText(
                matrixStack = matrices,
                font = CobbledResources.DEFAULT_LARGE,
                text = moveTemplate.displayName.bold(),
                x = x + 17,
                y = y + 2,
                opacity = moveSelection.opacity,
                shadow = true
            )

            drawScaledText(
                matrixStack = matrices,
                font = CobbledResources.DEFAULT_LARGE,
                text = (move.pp.toString() + "/" + move.maxpp.toString()).text().bold(),
                x = x + 75,
                y = y + 14,
                opacity = moveSelection.opacity,
                centered = true
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

//        if (!backButton.isHovered(mouseX.toDouble(), mouseY.toDouble())) {
            backButton.render(matrices, mouseX, mouseY, delta)
//        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val move = moveTiles.find { it.isHovered(mouseX, mouseY) }
        if (move != null) {
            move.onClick()
            return true
        } else if (backButton.isHovered(mouseX, mouseY)) {
            playDownSound(MinecraftClient.getInstance().soundManager)
            battleGUI.changeActionSelection(null)
        }
        return false
    }
}