/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.gui.summary.widgets.pages.moves.change

import com.cablemc.pokemod.common.PokemodNetwork
import com.cablemc.pokemod.common.api.gui.blitk
import com.cablemc.pokemod.common.api.moves.Move
import com.cablemc.pokemod.common.api.moves.MoveTemplate
import com.cablemc.pokemod.common.api.text.text
import com.cablemc.pokemod.common.client.PokemodClient
import com.cablemc.pokemod.common.client.gui.summary.widgets.ModelWidget
import com.cablemc.pokemod.common.client.gui.summary.widgets.pages.moves.MovesWidget
import com.cablemc.pokemod.common.client.render.drawScaledText
import com.cablemc.pokemod.common.net.messages.server.BenchMovePacket
import com.cablemc.pokemod.common.util.pokemodResource
import com.mojang.blaze3d.systems.RenderSystem
import java.math.RoundingMode
import java.text.DecimalFormat
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget
import net.minecraft.client.util.math.MatrixStack
class MoveSwitchPane(
    val movesWidget: MovesWidget,
    var replacedMove: Move
): AlwaysSelectedEntryListWidget<MoveSwitchPane.MoveObject>(
    MinecraftClient.getInstance(),
    PANE_WIDTH,
    PANE_HEIGHT,
    1,
    1 + PANE_HEIGHT,
    MOVE_HEIGHT
) {
    init {
        correctSize()
        setRenderHorizontalShadows(false)
        setRenderBackground(false)
        setRenderSelection(false)
    }

    val appropriateX: Int
        get() = client.window.scaledWidth / 2 + 13
    val appropriateY: Int
        get() = client.window.scaledHeight / 2 - 75

    fun correctSize() {
        updateSize(PANE_WIDTH, PANE_HEIGHT - 6, appropriateY, appropriateY + PANE_HEIGHT - 4)
        setLeftPos(appropriateX)
    }

    companion object {
        const val PANE_HEIGHT = 178
        const val MOVE_HEIGHT = 24
        const val MOVE_WIDTH = 112
        const val PANE_WIDTH = MOVE_WIDTH + 5
        private val switchPaneResource = pokemodResource("ui/summary/summary_moves_change.png")
        private val moveChangeEntryResource = pokemodResource("ui/summary/summary_moves_change_slot.png")
        private val moveChangeEntryOverlayResource = pokemodResource("ui/summary/summary_moves_change_slot_overlay.png")
        val df = DecimalFormat("#.##").also {
            it.roundingMode = RoundingMode.CEILING
        }
        private val typeResource = pokemodResource("ui/types.png")
    }

    public override fun addEntry(entry: MoveObject): Int {
        return super.addEntry(entry)
    }

    override fun getRowWidth(): Int {
        return MOVE_WIDTH
    }

    override fun getScrollbarPositionX(): Int {
        return left + width - 12
    }

    private fun scaleIt(i: Int): Int {
        return (client.window.scaleFactor * i).toInt()
    }

    override fun render(poseStack: MatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        correctSize()
        ModelWidget.render = false
        blitk(
            matrixStack = poseStack,
            texture = switchPaneResource,
            x = left,
            y = top - 4,
            height = PANE_HEIGHT,
            width = PANE_WIDTH
        )
        RenderSystem.enableScissor(scaleIt(left + 2), client.window.height / 2 - scaleIt(96), scaleIt(width - 4), scaleIt(height))
        super.render(poseStack, mouseX, mouseY, partialTicks)
        RenderSystem.disableScissor()
    }

    class MoveObject(val pane: MoveSwitchPane, val move: MoveTemplate, val ppRaisedStages: Int) : Entry<MoveObject>() {
        override fun getNarration() = move.displayName
        override fun render(
            poseStack: MatrixStack,
            index: Int,
            rowTop: Int,
            rowLeft: Int,
            rowWidth: Int,
            rowHeight: Int,
            mouseX: Int,
            mouseY: Int,
            isHovered: Boolean,
            partialTicks: Float
        ) {
            val rowTop = rowTop - 2
            val hex = move.elementalType.hue
            val r = ((hex shr 16) and 0b11111111) / 255.0
            val g = ((hex shr 8) and 0b11111111) / 255.0
            val b = (hex and 0b11111111) / 255.0

            val pp = move.pp + ppRaisedStages * move.pp / 5

            blitk(
                matrixStack = poseStack,
                texture = moveChangeEntryResource,
                x = rowLeft,
                y = rowTop,
                height = rowHeight,
                width = rowWidth - 15,
                red = r,
                green = g,
                blue = b
            )

            blitk(
                matrixStack = poseStack,
                texture = moveChangeEntryOverlayResource,
                x = rowLeft,
                y = rowTop,
                height = rowHeight,
                width = rowWidth - 15
            )

            val typeIconWidth = MOVE_HEIGHT - 4
            blitk(
                matrixStack = poseStack,
                texture = typeResource,
                x = rowLeft,
                y = rowTop,
                width = typeIconWidth, height = typeIconWidth,
                uOffset = typeIconWidth * move.elementalType.textureXMultiplier.toFloat() + 0.1,
                textureWidth = typeIconWidth * 18
            )

            val categoryHeight = 7
            blitk(
                matrixStack = poseStack,
                texture = move.damageCategory.resourceLocation,
                x = rowLeft + 23, y = rowTop + 3,
                width = 10, height = categoryHeight,
                vOffset = categoryHeight * move.damageCategory.textureXMultiplier,
                textureHeight = categoryHeight * 3
            )

            poseStack.push()
            val textScale = 0.6F
            poseStack.scale(textScale, textScale, 1F)
            drawScaledText(
                matrixStack = poseStack,
                text = move.displayName,
                x = (rowLeft + 37) / textScale,
                y = (rowTop + 4) / textScale,
                colour = 0,
                shadow = false
            )
            poseStack.pop()

            poseStack.push()
            val labelTextScale = 0.5F
            val labelY = rowTop + 13
            poseStack.scale(labelTextScale, labelTextScale, 1F)
            drawScaledText(
                matrixStack = poseStack,
                text = (if (move.power == 0.0) "—" else move.power.toInt().toString()).text(),
                x = (rowLeft + 30) / labelTextScale,
                y = labelY / labelTextScale,
                colour = 0xFFFFFF,
                centered = true
            )

            fun format(input: Double): String = if (input == -1.0 || input == 0.0) {
                "—"
            } else {
                "${df.format(input)}%"
            }
            drawScaledText(
                matrixStack = poseStack,
                text = format(move.accuracy).text(),
                x = (rowLeft + 49) / labelTextScale,
                y = labelY / labelTextScale,
                colour = 0xFFFFFF,
                centered = true
            )
            drawScaledText(
                matrixStack = poseStack,
                text = format(move.effectChance).text(),
                x = (rowLeft + 67) / labelTextScale,
                y = labelY / labelTextScale,
                colour = 0xFFFFFF,
                centered = true
            )
            drawScaledText(
                matrixStack = poseStack,
                text = pp.toString().text(),
                x = (rowLeft + 85) / labelTextScale,
                y = labelY / labelTextScale,
                colour = 0xFFFFFF,
                centered = true
            )
            poseStack.pop()
        }

        override fun mouseClicked(d: Double, e: Double, i: Int): Boolean {
            if (isMouseOver(d, e)) {
                val pokemon = pane.movesWidget.summary.currentPokemon
                val isParty = pokemon in PokemodClient.storage.myParty
                PokemodNetwork.sendToServer(
                    BenchMovePacket(
                        isParty = isParty,
                        uuid = pokemon.uuid,
                        oldMove = pane.replacedMove.template,
                        newMove = move
                    )
                )
                return true
            }
            return false
        }
    }
}