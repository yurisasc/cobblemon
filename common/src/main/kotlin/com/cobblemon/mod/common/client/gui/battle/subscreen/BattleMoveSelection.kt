/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.battle.subscreen

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.moves.Moves
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.api.text.gold
import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.battles.InBattleMove
import com.cobblemon.mod.common.battles.MoveActionResponse
import com.cobblemon.mod.common.battles.Targetable
import com.cobblemon.mod.common.client.CobblemonResources
import com.cobblemon.mod.common.client.battle.SingleActionRequest
import com.cobblemon.mod.common.client.gui.MoveCategoryIcon
import com.cobblemon.mod.common.client.gui.TypeIcon
import com.cobblemon.mod.common.client.gui.battle.BattleGUI
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.util.battleLang
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.math.toRGB
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.sound.SoundManager
import net.minecraft.text.Text
import net.minecraft.util.math.MathHelper.floor

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

        val moveTexture = cobblemonResource("textures/gui/battle/battle_move.png")
        val moveOverlayTexture = cobblemonResource("textures/gui/battle/battle_move_overlay.png")
    }

    val moveSet = request.moveSet!!
    val baseTiles = moveSet.moves.mapIndexed { index, inBattleMove ->
        val isEven = index % 2 == 0
        val x = if (isEven) this.x.toFloat() else this.x + MOVE_HORIZONTAL_SPACING + MOVE_WIDTH
        val y = if (index > 1) this.y + MOVE_HEIGHT + MOVE_VERTICAL_SPACING else this.y.toFloat()
        // if already dynamaxed, base tiles are the gimmick tiles
        if (moveSet.hasActiveGimmick())
            DynamaxButton.DynamaxTile(this, inBattleMove, x, y)
        else
            MoveTile(this, inBattleMove, x, y)
    }
    var moveTiles = baseTiles

    val backButton = BattleBackButton(x - 3F, MinecraftClient.getInstance().window.scaledHeight - 22F)
    val gimmickButtons = moveSet.getGimmicks().mapIndexed { index, gimmick ->
        val initOff = BattleBackButton.WIDTH * 0.65F
        val xOff = initOff + BattleGimmickButton.SPACING * index
        BattleGimmickButton.create(gimmick, this, backButton.x + xOff, backButton.y)
    }

    open class MoveTile(
        val moveSelection: BattleMoveSelection,
        val move: InBattleMove,
        val x: Float,
        val y: Float,
    ) {
        var moveTemplate = Moves.getByNameOrDummy(move.id)
        var rgb = moveTemplate.elementalType.hue.toRGB()

        open val targetList: List<Targetable>? get() = move.target.targetList(moveSelection.request.activePokemon)
        open val response: MoveActionResponse get() = MoveActionResponse(move.id, targetPnx)
        open val selectable: Boolean get() = !move.disabled

        val targetPnx: String? get() = targetList?.let { targets ->
            return@let when {
                targets.isEmpty() -> null
                targets.size == 1 -> targets[0].getPNX()
                else -> null    // TODO: multi-battles
            }
        }

        fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {

            val selectConditionOpacity = moveSelection.opacity * if (!selectable) 0.5F else 1F

            blitk(
                matrixStack = context.matrices,
                texture = moveTexture,
                x = x,
                y = y,
                width = MOVE_WIDTH,
                height = MOVE_HEIGHT,
                vOffset = if (selectable && isHovered(mouseX.toDouble(), mouseY.toDouble())) MOVE_HEIGHT else 0,
                textureHeight = MOVE_HEIGHT * 2,
                red = rgb.first,
                green = rgb.second,
                blue = rgb.third,
                alpha = selectConditionOpacity
            )

            blitk(
                matrixStack = context.matrices,
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
            ).render(context)

            // Move Category
            MoveCategoryIcon(
                x = x + 48,
                y = y + 14.5,
                category = moveTemplate.damageCategory,
                opacity = moveSelection.opacity
            ).render(context)

            drawScaledText(
                context = context,
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
                context = context,
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
            if (!selectable) return
            moveSelection.playDownSound(MinecraftClient.getInstance().soundManager)
            moveSelection.battleGUI.selectAction(moveSelection.request, response)
        }
    }

    override fun renderWidget(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        moveTiles.forEach {
            it.render(context, mouseX, mouseY, delta)
        }
        backButton.render(context.matrices, mouseX, mouseY, delta)
        gimmickButtons.forEach {
            it.render(context.matrices, mouseX, mouseY, delta)
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val move = moveTiles.find { it.isHovered(mouseX, mouseY) }
        val gimmick = gimmickButtons.find { it.isHovered(mouseX, mouseY) }
        if (move != null) {
            move.onClick()
            return true
        } else if (backButton.isHovered(mouseX, mouseY)) {
            playDownSound(MinecraftClient.getInstance().soundManager)
            battleGUI.changeActionSelection(null)
        } else if (gimmick != null) {
            gimmickButtons.filter { it != gimmick }.forEach { it.toggled = false }
            moveTiles = if (gimmick.toggle()) gimmick.tiles else baseTiles
        }
        return false
    }

    override fun playDownSound(soundManager: SoundManager) {
        soundManager.play(PositionedSoundInstance.master(CobblemonSounds.GUI_CLICK, 1.0F))
    }
}