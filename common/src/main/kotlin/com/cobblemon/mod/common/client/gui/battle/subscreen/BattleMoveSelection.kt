/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.battle.subscreen

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.gui.ColourLibrary
import com.cobblemon.mod.common.api.gui.MultiLineLabelK
import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.moves.Moves
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.api.text.gold
import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.battles.InBattleMove
import com.cobblemon.mod.common.battles.MoveActionResponse
import com.cobblemon.mod.common.client.CobblemonResources
import com.cobblemon.mod.common.client.battle.SingleActionRequest
import com.cobblemon.mod.common.client.gui.MoveCategoryIcon
import com.cobblemon.mod.common.client.gui.TypeIcon
import com.cobblemon.mod.common.client.gui.battle.BattleGUI
import com.cobblemon.mod.common.client.gui.summary.widgets.screens.moves.MovesWidget
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.util.battleLang
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.lang
import com.cobblemon.mod.common.util.math.toRGB
import net.minecraft.client.MinecraftClient
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.sound.SoundManager
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.math.MathHelper.floor
import java.math.RoundingMode
import java.text.DecimalFormat

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
        const val MOVE_DESC_SCALE = 0.5F

        private val decimalFormat = DecimalFormat("#.##").also {
            it.roundingMode = RoundingMode.CEILING
        }

        val moveTexture = cobblemonResource("ui/battle/battle_move.png")
        val moveOverlayTexture = cobblemonResource("ui/battle/battle_move_overlay.png")
        val moveDescriptionTexture = cobblemonResource("ui/battle/battle_move_desc.png")
    }

    val moveSet = request.moveSet!!
    val moveTiles = moveSet.moves.mapIndexed { index, inBattleMove ->
        val isEven = index % 2 == 0
        val x = if (isEven) this.x.toFloat() else this.x + MOVE_HORIZONTAL_SPACING + MOVE_WIDTH
        val y = if (index > 1) this.y + MOVE_HEIGHT + MOVE_VERTICAL_SPACING else this.y.toFloat()
        MoveTile(this, inBattleMove, x, y)
    }

    val backButton = BattleBackButton(x - 3F, MinecraftClient.getInstance().window.scaledHeight - 22F)

    class MoveTile(
        val moveSelection: BattleMoveSelection,
        val move: InBattleMove,
        val x: Float,
        val y: Float
    ) {
        val moveTemplate = Moves.getByNameOrDummy(move.id)
        val rgb = moveTemplate.elementalType.hue.toRGB()

        fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {

            val unselectable = move.disabled
            val selectConditionOpacity = moveSelection.opacity * if (unselectable) 0.5F else 1F

            blitk(
                matrixStack = matrices,
                texture = moveTexture,
                x = x,
                y = y,
                width = MOVE_WIDTH,
                height = MOVE_HEIGHT,
                vOffset = if (!unselectable && isHovered(mouseX.toDouble(), mouseY.toDouble())) MOVE_HEIGHT else 0,
                textureHeight = MOVE_HEIGHT * 2,
                red = rgb.first,
                green = rgb.second,
                blue = rgb.third,
                alpha = selectConditionOpacity
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

            // Type Icon
            TypeIcon(
                x = x - 9,
                y = y + 2,
                type = moveTemplate.elementalType,
                opacity = moveSelection.opacity
            ).render(matrices)

            // Move Category
            MoveCategoryIcon(
                x = x + 48,
                y = y + 14.5,
                category = moveTemplate.damageCategory,
                opacity = moveSelection.opacity
            ).render(matrices)

            drawScaledText(
                matrixStack = matrices,
                font = CobblemonResources.DEFAULT_LARGE,
                text = moveTemplate.displayName.bold(),
                x = x + 17,
                y = y + 2,
                opacity = selectConditionOpacity,
                shadow = true
            )

            var movePPText = Text.literal("${move.pp}/${move.maxpp}").bold()

            if (move.pp <= floor(move.maxpp / 2F)) {
                movePPText = if (move.pp == 0) movePPText.red() else movePPText.gold()
            }

            if (move.pp == 100 && move.maxpp == 100) {
                movePPText = "—/—".text().bold()
            }

            drawScaledText(
                matrixStack = matrices,
                font = CobblemonResources.DEFAULT_LARGE,
                text = movePPText,
                x = x + 75,
                y = y + 14,
                opacity = moveSelection.opacity,
                centered = true
            )
        }

        fun isHovered(mouseX: Double, mouseY: Double) = mouseX >= x && mouseX <= x + MOVE_WIDTH && mouseY >= y && mouseY <= y + MOVE_HEIGHT

        fun onClick() {
            if (move.disabled) {
                return
            }
            moveSelection.playDownSound(MinecraftClient.getInstance().soundManager)
            val targets = move.target.targetList(moveSelection.request.activePokemon)
            if (targets.isNullOrEmpty()) {
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

        backButton.render(matrices, mouseX, mouseY, delta)

        // Move Description
        val moveTile = moveTiles.find { it.isHovered(mouseX.toDouble(), mouseY.toDouble()) } ?: return
        blitk(
            matrixStack = matrices,
            texture = moveDescriptionTexture,
            x= mouseX,
            y = mouseY,
            width = 134,
            height = 40
        )

        // Move icons
        blitk(
            matrixStack = matrices,
            texture = MovesWidget.movesPowerIconResource,
            x= (mouseX + 7) / MOVE_DESC_SCALE,
            y = (mouseY + 6.5) / MOVE_DESC_SCALE,
            width = MovesWidget.MOVE_ICON_SIZE,
            height = MovesWidget.MOVE_ICON_SIZE,
            scale = MOVE_DESC_SCALE
        )

        blitk(
            matrixStack = matrices,
            texture = MovesWidget.movesAccuracyIconResource,
            x= (mouseX + 7) / MOVE_DESC_SCALE,
            y = (mouseY + 17.5) / MOVE_DESC_SCALE,
            width = MovesWidget.MOVE_ICON_SIZE,
            height = MovesWidget.MOVE_ICON_SIZE,
            scale = MOVE_DESC_SCALE
        )

        blitk(
            matrixStack = matrices,
            texture = MovesWidget.movesEffectIconResource,
            x= (mouseX + 7) / MOVE_DESC_SCALE,
            y = (mouseY + 28.5) / MOVE_DESC_SCALE,
            width = MovesWidget.MOVE_ICON_SIZE,
            height = MovesWidget.MOVE_ICON_SIZE,
            scale = MOVE_DESC_SCALE
        )

        drawScaledText(
            matrixStack = matrices,
            text = lang("ui.power"),
            x = mouseX + 14,
            y = mouseY + 7,
            scale = MOVE_DESC_SCALE,
            shadow = true
        )

        drawScaledText(
            matrixStack = matrices,
            text = lang("ui.accuracy"),
            x = mouseX + 14,
            y = mouseY + 18,
            scale = MOVE_DESC_SCALE,
            shadow = true
        )

        drawScaledText(
            matrixStack = matrices,
            text = lang("ui.effect"),
            x = mouseX + 14,
            y = mouseY + 29,
            scale = MOVE_DESC_SCALE,
            shadow = true
        )

        val mcFont = MinecraftClient.getInstance().textRenderer
        val movePower = if (moveTile.moveTemplate.power.toInt() > 0) moveTile.moveTemplate.power.toInt().toString().text() else "—".text()
        drawScaledText(
            matrixStack = matrices,
            text = movePower,
            x = (mouseX + 62.5) - (mcFont.getWidth(movePower) * MOVE_DESC_SCALE),
            y = mouseY + 7,
            scale = MOVE_DESC_SCALE,
            shadow = true
        )

        val moveAccuracy = format(moveTile.moveTemplate.accuracy).text()
        drawScaledText(
            matrixStack = matrices,
            text = moveAccuracy,
            x = (mouseX + 62.5) - (mcFont.getWidth(moveAccuracy) * MOVE_DESC_SCALE),
            y = mouseY + 18,
            scale = MOVE_DESC_SCALE,
            shadow = true
        )

        val moveEffect = format(moveTile.moveTemplate.effectChances.firstOrNull() ?: 0.0).text()
        drawScaledText(
            matrixStack = matrices,
            text = moveEffect,
            x = (mouseX + 62.5) - (mcFont.getWidth(moveEffect) * MOVE_DESC_SCALE),
            y = mouseY + 29,
            scale = MOVE_DESC_SCALE,
            shadow = true
        )

        matrices.push()
        matrices.scale(MOVE_DESC_SCALE, MOVE_DESC_SCALE, 1F)
        MultiLineLabelK.create(
            component = moveTile.moveTemplate.description,
            width = 57 / MOVE_DESC_SCALE,
            maxLines = 5
        ).renderLeftAligned(
            poseStack = matrices,
            x = (mouseX + 70) / MOVE_DESC_SCALE,
            y = (mouseY + 7) / MOVE_DESC_SCALE,
            ySpacing = 5.5 / MOVE_DESC_SCALE,
            colour = ColourLibrary.WHITE,
            shadow = true
        )
        matrices.pop()
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

    override fun playDownSound(soundManager: SoundManager) {
        soundManager.play(PositionedSoundInstance.master(CobblemonSounds.GUI_CLICK.get(), 1.0F))
    }

    private fun format(input: Double): String {
        if (input <= 0) return "—"
        return "${decimalFormat.format(input)}%"
    }
}