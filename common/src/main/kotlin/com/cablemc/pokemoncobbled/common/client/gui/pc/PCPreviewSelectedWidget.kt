/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.client.gui.pc

import com.cablemc.pokemoncobbled.common.api.storage.party.PartyPosition
import com.cablemc.pokemoncobbled.common.api.storage.pc.PCPosition
import com.cablemc.pokemoncobbled.common.client.gui.drawProfilePokemon
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.SoundlessWidget
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.PokemonFloatingState
import com.cablemc.pokemoncobbled.common.client.storage.ClientPC
import com.cablemc.pokemoncobbled.common.client.storage.ClientParty
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.math.Quaternion
import net.minecraft.util.math.Vec3f

class PCPreviewSelectedWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    val baseScale: Float = 2.7F,
    private val parent: PCWidget,
    private val pc: ClientPC,
    private val party: ClientParty,
): SoundlessWidget(pX, pY, pWidth, pHeight, Text.literal("PC - PreviewSelectedWidget")) {

    companion object {
        var render = true
    }

    var state = PokemonFloatingState()
    private val minecraft = MinecraftClient.getInstance()
    private var rotVec = Vec3f(13F, 35F, 0F)

    override fun render(pMatrixStack: MatrixStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        if (!render) {
            return
        }
        hovered = pMouseX >= x && pMouseY >= y && pMouseX < x + width && pMouseY < y + height
        renderPKM(pMatrixStack)
    }

    private fun renderPKM(poseStack: MatrixStack) {
        if (parent.selectedPosition == null) {
            return
        }

        val pokemon = when (parent.selectedPosition) {
            is PCPosition -> pc.get(parent.selectedPosition as PCPosition)
            is PartyPosition -> party.get(parent.selectedPosition as PartyPosition)
            else -> null
        } ?: return

        poseStack.push()

        RenderSystem.enableScissor(
            (x * minecraft.window.scaleFactor).toInt(),
            (minecraft.window.height - (y * minecraft.window.scaleFactor) - (height * minecraft.window.scaleFactor)).toInt(),
            (width * minecraft.window.scaleFactor).toInt(),
            (height * minecraft.window.scaleFactor).toInt()
        )

        poseStack.translate(x + width * 0.5, y.toDouble(), 0.0)
        poseStack.scale(baseScale, baseScale, baseScale)
        poseStack.push()

        drawProfilePokemon(
            renderablePokemon = pokemon.asRenderablePokemon(),
            matrixStack = poseStack,
            rotation = Quaternion.fromEulerXyzDegrees(rotVec),
            state = state
        )

        poseStack.pop()
        RenderSystem.disableScissor()

        poseStack.pop()
    }

    override fun onClick(pMouseX: Double, pMouseY: Double) {

    }
}