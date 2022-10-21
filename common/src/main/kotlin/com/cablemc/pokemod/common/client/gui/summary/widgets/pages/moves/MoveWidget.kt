/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.gui.summary.widgets.pages.moves

import com.cablemc.pokemod.common.api.gui.ColourLibrary
import com.cablemc.pokemod.common.api.gui.blitk
import com.cablemc.pokemod.common.api.moves.Move
import com.cablemc.pokemod.common.api.text.bold
import com.cablemc.pokemod.common.client.PokemodResources
import com.cablemc.pokemod.common.client.gui.summary.widgets.SoundlessWidget
import com.cablemc.pokemod.common.client.gui.summary.widgets.pages.moves.SwitchMoveButton.Companion.SWITCH_MOVE_BUTTON_HEIGHT
import com.cablemc.pokemod.common.client.gui.summary.widgets.pages.moves.SwitchMoveButton.Companion.SWITCH_MOVE_BUTTON_WIDTH
import com.cablemc.pokemod.common.client.gui.summary.widgets.pages.moves.change.MoveSwitchPane
import com.cablemc.pokemod.common.client.gui.summary.widgets.type.SingleTypeWidget
import com.cablemc.pokemod.common.client.render.drawScaledText
import com.cablemc.pokemod.common.util.pokemodResource
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
class MoveWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    val move: Move,
    infoX: Int, infoY: Int,
    private val movesWidget: MovesWidget,
    private val index: Int
): SoundlessWidget(pX, pY, pWidth, pHeight, Text.literal(move.name)) {

    companion object {
        private val moveResource = pokemodResource("ui/summary/summary_moves_slot.png")
        private val moveOverlayResource = pokemodResource("ui/summary/summary_moves_slot_overlay.png")
        private val movePpResource = pokemodResource("ui/summary/summary_moves_overlay_pp.png")
        private const val PP_WIDTH_DIFF = 3
        private const val PP_HEIGHT = 6.85F
        private const val PP_HEIGHT_DIFF = 22.5F

        private const val MOVE_BUTTON_WIDTH = 12
        private const val MOVE_BUTTON_HEIGHT = 11
        private const val MOVE_UP_BUTTON_Y_OFFSET = 2
        private const val MOVE_DOWN_BUTTON_Y_OFFSET = 14

        private const val MOVE_NAME_COLOUR = 0x0A0A0A
        private const val MOVE_WIDTH = 119F
        private const val MOVE_HEIGHT = 23F

        private const val TYPE_WIDGET_Y_OFFSET = 2
    }

    private val typeWidget = SingleTypeWidget(x + 2, y + TYPE_WIDGET_Y_OFFSET, 19, 19, move.type)
    private val moveInfoWidget = MoveInfoWidget(x, y, width, height, move, infoX, infoY)
    private val moveUpButton = MovesMoveButton(x - 15, y + MOVE_UP_BUTTON_Y_OFFSET, MOVE_BUTTON_WIDTH, MOVE_BUTTON_HEIGHT, true) {
        movesWidget.moveMove(this, true)
    }.apply {
        addWidget(this)
    }
    private val moveDownButton = MovesMoveButton(x - 15, y + MOVE_DOWN_BUTTON_Y_OFFSET + 4, MOVE_BUTTON_WIDTH, MOVE_BUTTON_HEIGHT, false) {
        movesWidget.moveMove(this, false)
    }.apply {
        addWidget(this)
    }

    private val switchMoveButton = SwitchMoveButton(x + 123, y + 4, SWITCH_MOVE_BUTTON_WIDTH, SWITCH_MOVE_BUTTON_HEIGHT, 0, 0, 4, move.template, movesWidget) {
        val pane = movesWidget.moveSwitchPane
        if (pane == null || pane.replacedMove != this.move) {
            movesWidget.moveSwitchPane = MoveSwitchPane(
                movesWidget = movesWidget,
                replacedMove = move
            ).also { switchPane ->
                val pokemon = movesWidget.summary.currentPokemon
                pokemon.allAccessibleMoves
                    .filter { template -> pokemon.moveSet.none { it.template == template } }
                    .map { template ->
                        val benched = pokemon.benchedMoves.find { it.moveTemplate == template }
                        MoveSwitchPane.MoveObject(switchPane, template, benched?.ppRaisedStages ?: 0)
                    }
                    .forEach { switchPane.addEntry(it) }
            }
        } else {
            movesWidget.closeSwitchMoveMenu()
        }


    }.apply {
        addWidget(this)
    }

    fun update() {
        typeWidget.y = y + TYPE_WIDGET_Y_OFFSET
        moveUpButton.y = y + MOVE_UP_BUTTON_Y_OFFSET
        moveDownButton.y = y + MOVE_DOWN_BUTTON_Y_OFFSET
    }

    override fun render(pMatrixStack: MatrixStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        hovered = pMouseX >= x && pMouseY >= y && pMouseX < x + width && pMouseY < y + height
        // Rendering Move Texture

        val hex = move.type.hue
        val r = ((hex shr 16) and 0b11111111) / 255.0
        val g = ((hex shr 8) and 0b11111111) / 255.0
        val b = (hex and 0b11111111) / 255.0

        blitk(
            matrixStack = pMatrixStack,
            texture = moveResource,
            x = x + 0.8F, y = y,
            red = r, green = g, blue = b,
            width = MOVE_WIDTH, height = MOVE_HEIGHT
        )

        blitk(
            matrixStack = pMatrixStack,
            texture = moveOverlayResource,
            x = x + 0.8F, y = y,
            width = MOVE_WIDTH, height = MOVE_HEIGHT
        )

        // Rendering PP Texture
        blitk(
            matrixStack = pMatrixStack,
            texture = movePpResource,
            x = (x + PP_WIDTH_DIFF),
            y = y + PP_HEIGHT_DIFF,
            height = PP_HEIGHT,
            width = ((width - PP_WIDTH_DIFF * 2) * getPpAsPercentage(move)) + 1.25F,
            textureWidth = width - 4.75F
        )


        // Render remaining PP Text
        drawScaledText(
            matrixStack = pMatrixStack,
            text = Text.literal("${move.currentPp} / ${move.maxPp}"),
            x = x + width / 2, y = y + 24,
            colour = ColourLibrary.WHITE, shadow = false,
            centered = true,
            scale = 0.5F
        )

        // Render Type Icon
        typeWidget.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks)

        switchMoveButton.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks)

        // Render Damage Category
        val dmgCatWidth = 28.00
        val dmgCatHeight = 7.5
        blitk(
            matrixStack = pMatrixStack,
            texture = move.damageCategory.resourceLocation,
            x = x + 25, y = y + 13,
            width = dmgCatWidth, height = dmgCatHeight,
            vOffset = dmgCatHeight * move.damageCategory.textureXMultiplier,
            textureHeight = dmgCatHeight * 3
        )

        // Render Move Name
        drawScaledText(
            matrixStack = pMatrixStack,
            text = move.displayName.bold(),
            font = PokemodResources.DEFAULT_LARGE,
            x = x + 88, y = y + 7,
            maxCharacterWidth = 50,
            colour = MOVE_NAME_COLOUR,
            shadow = false,
            centered = true
        )

        // Render Move Info
        if (isHovered) {
            moveInfoWidget.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks)
        }

        // Render Move Move Button
        moveUpButton.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks)
        moveDownButton.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks)
    }

    // Get the remaining PP as percentage
    private fun getPpAsPercentage(move: Move): Double {
        return move.currentPp.toDouble() / move.maxPp.toDouble()
    }

    private fun specificOffset(pos: Int): Float {
        when (pos) {
            0 -> return 0F
            1 -> return 0F
            2 -> return 0F
            3 -> return 0F
        }
        return 0F
    }
}