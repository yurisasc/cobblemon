/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.client.gui.pc

import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.api.storage.party.PartyPosition
import com.cablemc.pokemoncobbled.common.client.gui.drawProfilePokemon
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.SoundlessWidget
import com.cablemc.pokemoncobbled.common.client.storage.ClientPC
import com.cablemc.pokemoncobbled.common.client.storage.ClientParty
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.cablemc.pokemoncobbled.common.util.scaleIt
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.Quaternion
import net.minecraft.util.math.Vec3f

class PartyMemberWidget(
    x: Int, y: Int,
    private val pcGui: PCGui,
    private val pc: ClientPC,
    private val party: ClientParty,
    val position: PartyPosition,
    private val texture: Identifier,
    onPress: PressAction
) : ButtonWidget(x - PC_BOX_DIMENSION, y, PC_BOX_DIMENSION, PC_BOX_DIMENSION, Text.literal("PartyMember"), onPress) {

    companion object {
        // Box slot
        private const val PC_BOX_DIMENSION = 32

        // Portrait
        private const val PORTRAIT_DIMENSIONS = 27

        // Slot Textures
        val slotOneResource = cobbledResource("ui/pc/pc_party_1.png")
        val slotTwoThroughFiveResource = cobbledResource("ui/pc/pc_party_2-5.png")
        val slotSixResource = cobbledResource("ui/pc/pc_party_6.png")
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        matrices.push()

        val xOffset = if (texture == slotSixResource) -2f else 0f
        val slotWidth = if (texture == slotSixResource) 35 else 33
        blitk(
            matrixStack = matrices,
            x = x + xOffset + -0.9f, y = y - 1F,
            texture = this.texture,
            width = slotWidth, height = 33
        )

        val pokemon = party.get(position)
        if (pokemon != null) {
            val pokemonX = this.x - 3
            val pokemonY = this.y - 4

            val minecraft = MinecraftClient.getInstance()
            RenderSystem.enableScissor(
                this.scaleIt(pokemonX + 3),
                minecraft.window.height - this.scaleIt(pokemonY + PORTRAIT_DIMENSIONS + 2),
                this.scaleIt(PORTRAIT_DIMENSIONS),
                this.scaleIt(PORTRAIT_DIMENSIONS)
            )
            matrices.translate(pokemonX + (PORTRAIT_DIMENSIONS / 2.0) + 4, pokemonY + 4.0, 0.0)
            matrices.scale(2.5F, 2.5F, 1F)
            drawProfilePokemon(
                renderablePokemon = pokemon.asRenderablePokemon(),
                matrixStack = matrices,
                rotation = Quaternion.fromEulerXyzDegrees(Vec3f(13F, 35F, 0F)),
                state = null,
                scale = 6F
            )
            RenderSystem.disableScissor()
        }

        matrices.pop()
    }

}