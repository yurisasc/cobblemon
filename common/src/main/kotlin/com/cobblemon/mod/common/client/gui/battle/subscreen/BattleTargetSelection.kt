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
import com.cobblemon.mod.common.api.moves.MoveTemplate
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.battles.InBattleMove
import com.cobblemon.mod.common.battles.MoveActionResponse
import com.cobblemon.mod.common.client.CobblemonResources
import com.cobblemon.mod.common.client.battle.ActiveClientBattlePokemon
import com.cobblemon.mod.common.client.battle.SingleActionRequest
import com.cobblemon.mod.common.client.gui.battle.BattleGUI
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.util.battleLang
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.math.toRGB
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.sound.SoundManager

class BattleTargetSelection(
        battleGUI: BattleGUI,
        request: SingleActionRequest,
        move: InBattleMove
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
        const val TARGET_WIDTH = 92
        const val TARGET_HEIGHT = 24
        const val MOVE_VERTICAL_SPACING = 5F
        const val MOVE_HORIZONTAL_SPACING = 13F

        val targetTexture = cobblemonResource("textures/gui/battle/battle_move.png")
        val moveOverlayTexture = cobblemonResource("textures/gui/battle/battle_move_overlay.png")
    }

    val move = move
    val targets = request.activePokemon.getAllActivePokemon()
    val baseTiles = targets.mapIndexed { index, target ->
        val isAlly = target.getPNX()[1] == request.activePokemon.getPNX()[1] //TODO Multi-battle
        val fieldPos = target.getPNX()[2] - 'a'
        val x = this.x + MOVE_HORIZONTAL_SPACING + fieldPos * TARGET_WIDTH
        val y = if (isAlly) this.y + TARGET_HEIGHT + MOVE_VERTICAL_SPACING else this.y.toFloat()
        TargetTile(this, target, x, y)
    }
    var targetTiles = baseTiles

    val backButton = BattleBackButton(x - 3F, MinecraftClient.getInstance().window.scaledHeight - 22F)


    open class TargetTile(
            val targetSelection: BattleTargetSelection,
            val target: ActiveClientBattlePokemon,
            val x: Float,
            val y: Float,
    ) {
        var moveTemplate = MoveTemplate.dummy(target.battlePokemon?.displayName.toString()) // Moves.getByNameOrDummy(target.id)

        //open val targetList: List<Targetable>? get() = target.targetList(moveSelection.request.activePokemon)

//        val targetPnx: String? get() = targetSelection.move.target.targetList?.let { targets ->
//            return@let when {
//                targets.isEmpty() -> null
//                targets.size == 1 -> targets[0].getPNX()
//                else -> null    // TODO: multi-battles
//            }
//        }
        open val response: MoveActionResponse get() = MoveActionResponse(targetSelection.move.id, targetSelection.move.target.targetList(targetSelection.request.activePokemon)?.firstOrNull { it.getPNX() == target.getPNX() }?.getPNX())
        open val selectable: Boolean get() = true
        val hue = target.getHue()
        val rgb = Triple(((hue shr 16) and 0b11111111) / 255F, ((hue shr 8) and 0b11111111) / 255F, (hue and 0b11111111) / 255F)

        val targetPnx: String get() = target.getPNX()

        fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {

            val selectConditionOpacity = targetSelection.opacity * if (!selectable) 0.5F else 1F

            blitk(
                matrixStack = context.matrices,
                texture = targetTexture,
                x = x,
                y = y,
                width = TARGET_WIDTH,
                height = TARGET_HEIGHT,
                vOffset = if (selectable && isHovered(mouseX.toDouble(), mouseY.toDouble())) TARGET_HEIGHT else 0,
                textureHeight = TARGET_HEIGHT * 2,
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
                width = TARGET_WIDTH,
                height = TARGET_HEIGHT,
                alpha = targetSelection.opacity
            )


            target.battlePokemon?.displayName?.bold()?.let {
                drawScaledText(
                    context = context,
                    font = CobblemonResources.DEFAULT_LARGE,
                    text = it,
                    x = x + 17,
                    y = y + 2,
                    opacity = selectConditionOpacity,
                    shadow = true
                )
            }
        }

        fun isHovered(mouseX: Double, mouseY: Double) = mouseX >= x && mouseX <= x + TARGET_WIDTH && mouseY >= y && mouseY <= y + TARGET_HEIGHT

        fun onClick() {
            if (!selectable) return
            targetSelection.playDownSound(MinecraftClient.getInstance().soundManager)
            targetSelection.battleGUI.selectAction(targetSelection.request, response)
        }
    }

    override fun renderButton(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        targetTiles.forEach {
            it.render(context, mouseX, mouseY, delta)
        }
        backButton.render(context.matrices, mouseX, mouseY, delta)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val target = targetTiles.find { it.isHovered(mouseX, mouseY) }
        if (target != null) {
            target.onClick()
            return true
        } else if (backButton.isHovered(mouseX, mouseY)) {
            playDownSound(MinecraftClient.getInstance().soundManager)
            battleGUI.changeActionSelection(null)
        }
        return false
    }

    override fun playDownSound(soundManager: SoundManager) {
        soundManager.play(PositionedSoundInstance.master(CobblemonSounds.GUI_CLICK, 1.0F))
    }
}