/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.tm

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.client.gui.drawProfilePokemon
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.math.fromEulerXYZDegrees
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Text
import org.joml.Quaternionf
import org.joml.Vector3f

class TMPartySlotWidget(
    pX: Number,
    pY: Number,
    val pokemon: Pokemon?,
    onPress: PressAction
) : ButtonWidget(pX.toInt(), pY.toInt(), WIDTH, HEIGHT, Text.literal("PartyMember"), onPress, NarrationSupplier { "".text() }) {

    companion object {
        const val WIDTH = 46
        const val HEIGHT = 27
        private const val PORTRAIT_DIAMETER = 25

        private val slotResource = cobblemonResource("textures/gui/tm/party_slot.png")
        val genderIconMale = cobblemonResource("textures/gui/party/party_gender_male.png")
        val genderIconFemale = cobblemonResource("textures/gui/party/party_gender_female.png")
    }
    private fun getSlotVOffset(pokemon: Pokemon?, isHovered: Boolean, isSelected: Boolean): Int {
        if (isHovered || isSelected) {
            if (pokemon == null) {
                return 0
            }
            return height
        }
        return 0
    }

    override fun renderButton(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
        val matrices = context.matrices

        blitk(
            matrixStack = matrices,
            texture = slotResource,
            x = x,
            y = y,
            width = width,
            height = height,
            vOffset = getSlotVOffset(pokemon, isHovered, isSelected),
            textureHeight = height * 2,
        )

        if (pokemon != null) {
            val halfScale = 0.5F

            // Render Pokémon
            matrices.push()
            matrices.translate(x + (PORTRAIT_DIAMETER / 2.0), y - 1.0, 0.0)
            matrices.scale(2.5F, 2.5F, 1F)
            drawProfilePokemon(
                species = pokemon.species.resourceIdentifier,
                aspects = pokemon.aspects.toSet(),
                matrixStack = matrices,
                rotation = Quaternionf().fromEulerXYZDegrees(Vector3f(13F, 35F, 0F)),
                state = null,
                scale = 4.5F,
                partialTicks = delta
            )
            matrices.pop()

            // Draw Name
            drawScaledText(
                context = context,
                text = pokemon.getDisplayName(),
                x = x + 16,
                y = y + 21.5,
                scale = halfScale
            )

            val ballIcon = cobblemonResource("textures/gui/ball/" + pokemon.caughtBall.name.path + ".png")
            val ballHeight = 22
            blitk(
                matrixStack = matrices,
                texture = ballIcon,
                x = (x - 2) / halfScale,
                y = (y - 3) / halfScale,
                height = ballHeight,
                width = 18,
                textureHeight = ballHeight * 2,
                scale = halfScale
            )
        }
    }
}