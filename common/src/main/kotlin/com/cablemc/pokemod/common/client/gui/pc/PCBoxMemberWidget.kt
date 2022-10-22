/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.gui.pc

import com.cablemc.pokemod.common.api.storage.pc.PCPosition
import com.cablemc.pokemod.common.client.gui.drawProfilePokemon
import com.cablemc.pokemod.common.client.storage.ClientPC
import com.cablemc.pokemod.common.util.scaleIt
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.math.Quaternion
import net.minecraft.util.math.Vec3f

class PCBoxMemberWidget(
    x: Int, y: Int,
    private val pcGui: PCGui,
    private val pc: ClientPC,
    val position: PCPosition,
    onPress: PressAction
) : ButtonWidget(x - PC_BOX_DIMENSION, y, PC_BOX_DIMENSION, PC_BOX_DIMENSION, Text.literal("PCBoxMember"), onPress) {

    companion object {
        // Box slot
        private const val PC_BOX_DIMENSION = 32

        // Portrait
        private const val PORTRAIT_DIMENSIONS = 27
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        val pokemon = pc.get(position) ?: return
        matrices.push()
        val minecraft = MinecraftClient.getInstance()
        RenderSystem.enableScissor(
            this.scaleIt(this.x + 3),
            minecraft.window.height - this.scaleIt(this.y + PORTRAIT_DIMENSIONS + 2),
            this.scaleIt(PORTRAIT_DIMENSIONS),
            this.scaleIt(PORTRAIT_DIMENSIONS)
        )
        matrices.translate(this.x + (PORTRAIT_DIMENSIONS / 2.0) + 4, this.y + 4.0, 0.0)
        matrices.scale(2.5F, 2.5F, 1F)
        drawProfilePokemon(
            renderablePokemon = pokemon.asRenderablePokemon(),
            matrixStack = matrices,
            rotation = Quaternion.fromEulerXyzDegrees(Vec3f(13F, 35F, 0F)),
            state = null,
            scale = 6F
        )
        RenderSystem.disableScissor()
        matrices.pop()
    }

}