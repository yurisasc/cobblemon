/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.trade

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.gui.drawProfilePokemon
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.client.render.renderScaledGuiItemIcon
import com.cobblemon.mod.common.net.messages.client.trade.TradeStartedPacket
import com.cobblemon.mod.common.api.pokemon.gender.Gender
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.lang
import com.cobblemon.mod.common.util.math.fromEulerXYZDegrees
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.sound.SoundManager
import net.minecraft.text.Text
import org.joml.Quaternionf
import org.joml.Vector3f

open class PartySlot(
    x: Int, y: Int,
    private val pokemon: TradeStartedPacket.TradeablePokemon?,
    private val parent: TradeGUI,
    private val isOpposing: Boolean = false,
    onPress: PressAction
) : ButtonWidget(x, y, SIZE, SIZE, Text.literal("PartySlot"), onPress, DEFAULT_NARRATION_SUPPLIER) {

    companion object {
        const val SIZE = 25

        private val hoverBackgroundResource = cobblemonResource("textures/gui/trade/trade_party_slot_hover.png")
        private val genderIconMale = cobblemonResource("textures/gui/pc/gender_icon_male.png")
        private val genderIconFemale = cobblemonResource("textures/gui/pc/gender_icon_female.png")
        private val selectPointerResource = cobblemonResource("textures/gui/pc/pc_pointer.png")
        private val untradeableResource = cobblemonResource("textures/gui/trade/trade_slot_icon_locked.png")
    }

    override fun playDownSound(soundManager: SoundManager) {
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val matrices = context.matrices
        if (!isOpposing && isHovered(mouseX, mouseY)) {
            blitk(
                matrixStack = matrices,
                texture = hoverBackgroundResource,
                x = x,
                y = y,
                width = SIZE,
                height = SIZE
            )
        }

        if (pokemon != null) {
            context.enableScissor(
                x - 2,
                y + 2,
                x + SIZE + 4,
                y + SIZE + 4
            )

            // Render Pokémon
            matrices.push()
            matrices.translate(x + (SIZE / 2.0), y + 1.0, 0.0)
            matrices.scale(2.5F, 2.5F, 1F)
            drawProfilePokemon(
                renderablePokemon = pokemon.asRenderablePokemon(),
                matrixStack = matrices,
                rotation = Quaternionf().fromEulerXYZDegrees(Vector3f(13F, 35F, 0F)),
                state = null,
                scale = 4.5F,
                partialTicks = delta
            )
            matrices.pop()

            context.disableScissor()

            // Ensure elements are not hidden behind Pokémon render
            matrices.push()
            matrices.translate(0.0, 0.0, 100.0)
            // Level
            drawScaledText(
                context = context,
                text = lang("ui.lv.number", pokemon.level),
                x = x + 1,
                y = y + 1,
                shadow = true,
                scale = TradeGUI.SCALE
            )

            if (pokemon.gender != Gender.GENDERLESS) {
                blitk(
                    matrixStack = matrices,
                    texture = if (pokemon.gender == Gender.MALE) genderIconMale else genderIconFemale,
                    x = (x + 21) / TradeGUI.SCALE,
                    y = (y + 1) / TradeGUI.SCALE,
                    width = 6,
                    height = 8,
                    scale = TradeGUI.SCALE
                )
            }
            if (!pokemon.tradeable) {
                matrices.push()
                matrices.translate(0F, 0F, 10F)
                blitk(
                    matrixStack = matrices,
                    texture = untradeableResource,
                    x = (x + 8) / TradeGUI.SCALE,
                    y = (y + 8) / TradeGUI.SCALE,
                    width = 20,
                    height = 20,
                    scale = TradeGUI.SCALE
                )
                matrices.pop()
            }

            matrices.pop()
            if (hasSelected()) {
                blitk(
                    matrixStack = matrices,
                    texture = selectPointerResource,
                    x = (x + 10) / TradeGUI.SCALE,
                    y = ((y - 3) / TradeGUI.SCALE) - parent.selectPointerOffsetY,
                    width = 11,
                    height = 8,
                    scale = TradeGUI.SCALE
                )
            }

            // Held Item
            val heldItem = pokemon.heldItem
            if (!heldItem.isEmpty) {
                renderScaledGuiItemIcon(
                    itemStack = heldItem,
                    x = x + 16.0,
                    y = y + 16.0,
                    scale = 0.5,
                    matrixStack = matrices
                )
            }
        }
    }

    open fun hasSelected(): Boolean {
        val offeredPokemon = if (isOpposing) parent.trade.oppositeOffer.get() else parent.trade.myOffer.get()
        return pokemon?.pokemonId == offeredPokemon?.uuid && pokemon != null
    }

    fun isHovered(mouseX: Int, mouseY: Int) = pokemon?.tradeable != false && mouseX.toFloat() in (x.toFloat()..(x.toFloat() + SIZE)) && mouseY.toFloat() in (y.toFloat()..(y.toFloat() + SIZE))
}