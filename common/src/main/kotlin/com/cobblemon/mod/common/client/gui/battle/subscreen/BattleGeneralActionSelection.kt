/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.battle.subscreen

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.cobblemon.mod.common.client.CobblemonClient
import com.cobblemon.mod.common.client.battle.SingleActionRequest
import com.cobblemon.mod.common.client.gui.battle.BattleGUI
import com.cobblemon.mod.common.client.gui.battle.widgets.BattleOptionTile
import com.cobblemon.mod.common.util.battleLang
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Selectable
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.sound.SoundManager
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.MutableText
import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.render.*

class BattleGeneralActionSelection(
    battleGUI: BattleGUI,
    request: SingleActionRequest
) : BattleActionSelection(
    battleGUI,
    request,
    BattleGUI.OPTION_ROOT_X,
    MinecraftClient.getInstance().window.scaledHeight - BattleGUI.OPTION_VERTICAL_OFFSET,
    (BattleOptionTile.OPTION_WIDTH + 3 * BattleGUI.OPTION_HORIZONTAL_SPACING).toInt(),
    (BattleOptionTile.OPTION_HEIGHT + 3 * BattleGUI.OPTION_VERTICAL_SPACING).toInt(),
    battleLang("choose_action")
) {
    val tiles = mutableListOf<BattleOptionTile>()
    init {
        var rank = 0

        addOption(rank++, battleLang("ui.fight"), BattleGUI.fightResource) {
            playDownSound(MinecraftClient.getInstance().soundManager)
            battleGUI.changeActionSelection(BattleMoveSelection(battleGUI, request))
        }

        if (request.moveSet?.trapped != true) {
            addOption(rank++, battleLang("ui.switch"), BattleGUI.switchResource) {
                battleGUI.changeActionSelection(BattleSwitchPokemonSelection(battleGUI, request))
                playDownSound(MinecraftClient.getInstance().soundManager)
            }
        }

        CobblemonClient.battle?.let { battle ->
            if (battle.battleFormat.battleType.pokemonPerSide == 1 && battle.side2.actors.first().type == ActorType.WILD) {
                addOption(rank++, battleLang("ui.capture"), BattleGUI.bagResource) {
                    CobblemonClient.battle?.minimised = true
                    MinecraftClient.getInstance().player?.sendMessage(battleLang("throw_pokeball_prompt"), false)
                    playDownSound(MinecraftClient.getInstance().soundManager)
                }

                addOption(rank++, battleLang("ui.run"), BattleGUI.runResource) {
                    CobblemonClient.battle?.minimised = true
                    MinecraftClient.getInstance().player?.sendMessage(battleLang("run_prompt"), false)
                    playDownSound(MinecraftClient.getInstance().soundManager)
                }
            }
        }
    }

    private fun addOption(rank: Int, text: MutableText, texture: Identifier, onClick: () -> Unit) {
        val startY = MinecraftClient.getInstance().window.scaledHeight - BattleGUI.OPTION_VERTICAL_OFFSET
        val x = if (rank % 2 == 0) BattleGUI.OPTION_ROOT_X else BattleGUI.OPTION_ROOT_X + BattleGUI.OPTION_HORIZONTAL_SPACING + BattleOptionTile.OPTION_WIDTH
        val y = if (rank > 1) startY + BattleOptionTile.OPTION_HEIGHT + BattleGUI.OPTION_HORIZONTAL_SPACING else startY
        tiles.add(
            BattleOptionTile(
                battleGUI = battleGUI,
                x = x,
                y = y,
                resource = texture,
                text = text,
                onClick = onClick
            )
        )
    }

    override fun renderButton(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        for ((index, tile) in tiles.withIndex()) {
            if (index == focusedIndex) {
                // Set the color for the highlight based on the index
                /*val color = when (index) {
                    0 -> rgbToARGBColor(176, 78, 79) // Fight - Red
                    1 -> rgbToARGBColor(71, 167, 66) // Bag - Green
                    2 -> rgbToARGBColor(187, 159, 48) // Switch - Yellow
                    3 -> rgbToARGBColor(60, 143, 170) // Run - Blue
                    else -> rgbToARGBColor(255, 255, 255) // White
                }*/
                val color = when (index) {
                    0 -> Triple((176 / 255).toFloat(), (78 / 255).toFloat(), (79 / 255).toFloat()) // Fight - Red
                    1 -> Triple((71 / 255).toFloat(), (167 / 255).toFloat(), (66 / 255).toFloat()) // Bag - Green
                    2 -> Triple((187 / 255).toFloat(), (159 / 255).toFloat(), (48 / 255).toFloat()) // Switch - Yellow
                    3 -> Triple((60 / 255).toFloat(), (143 / 255).toFloat(), (170 / 255).toFloat()) // Run - Blue
                    else -> Triple((255 / 255).toFloat(), (255 / 255).toFloat(), (255 / 255).toFloat()) // White
                }


                // Draw a rectangle around the tile as a highlight and adjust the padding or size
                val padding = 2 // pixels
                val rectX = tile.x - padding
                val rectY = tile.y - padding
                val rectWidth = BattleOptionTile.OPTION_WIDTH + 2 * padding
                val rectHeight = BattleOptionTile.OPTION_HEIGHT + 2 * padding

                val tessellator = Tessellator.getInstance()
                val bufferBuilder = tessellator.buffer

                RenderSystem.setShader { GameRenderer.getPositionTexProgram() } // set shader for texture
                RenderSystem.setShaderTexture(0, BattleGUI.menuHighlightResource) // load menu highlight and set shader for it
                RenderSystem.setShaderColor(color.first, color.second, color.third, 1F) // Apply the tint color

                bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE)
                bufferBuilder.vertex(rectX.toDouble(), (rectY + rectHeight).toDouble(), 0.0).texture(0f, 1f).next() // White color here
                bufferBuilder.vertex((rectX + rectWidth).toDouble(), (rectY + rectHeight).toDouble(), 0.0).texture(0f, 1f).next()
                bufferBuilder.vertex((rectX + rectWidth).toDouble(), rectY.toDouble(), 0.0).texture(0f, 1f).next()
                bufferBuilder.vertex(rectX.toDouble(), rectY.toDouble(), 0.0).texture(0f, 1f).next()
                tessellator.draw()
            }
            tile.render(context, mouseX, mouseY, delta)
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        return tiles.any { it.mouseClicked(mouseX, mouseY, button) }
    }

    override fun appendDefaultNarrations(builder: NarrationMessageBuilder) {
    }

    override fun playDownSound(soundManager: SoundManager) {
        soundManager.play(PositionedSoundInstance.master(CobblemonSounds.GUI_CLICK, 1.0F))
    }

    fun rgbToARGBColor(red: Int, green: Int, blue: Int, alpha: Int = 255): Int {
        return (alpha shl 24) or (red shl 16) or (green shl 8) or blue
    }

    override fun getType() = Selectable.SelectionType.NONE

    // Battle UI Arrow Key navigation code
    private var focusedIndex: Int = 0
    fun changeFocus(keyCode: Int) {
        val numRows = 2 // 2 rows for now
        val numColumns = tiles.size / numRows

        val currentRow = focusedIndex / numColumns
        val currentColumn = focusedIndex % numColumns

        when (keyCode) {
            GLFW.GLFW_KEY_UP -> {
                if (currentRow > 0) focusedIndex -= numColumns
            }
            GLFW.GLFW_KEY_DOWN -> {
                if (currentRow < numRows - 1) focusedIndex += numColumns
            }
            GLFW.GLFW_KEY_LEFT -> {
                if (currentColumn > 0) focusedIndex -= 1
            }
            GLFW.GLFW_KEY_RIGHT -> {
                if (currentColumn < numColumns - 1) focusedIndex += 1
            }
        }

        // To make sure focusedIndex stays within the bounds of the buttons
        focusedIndex = focusedIndex.coerceIn(0, tiles.size - 1)
    }


    fun triggerFocusedButton() {
        tiles.getOrNull(focusedIndex)?.onClick?.invoke()
    }


}