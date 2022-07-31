package com.cablemc.pokemoncobbled.common.client.gui.summary.widgets

import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.client.CobbledResources
import com.cablemc.pokemoncobbled.common.client.gui.drawProfilePokemon
import com.cablemc.pokemoncobbled.common.client.gui.summary.Summary
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.pages.info.evolution.EvolutionListScrollPane
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.cablemc.pokemoncobbled.common.util.scaleIt
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText
import net.minecraft.util.Identifier
import net.minecraft.util.math.Quaternion
import net.minecraft.util.math.Vec3f

class PartyMemberWidget(
    x: Int, y: Int,
    private val summary: Summary,
    private val pokemon: Pokemon?,
    private val index: Int,
    private val isClientPartyMember: Boolean,
    private val partySize: Int
) : SoundlessWidget(x - PARTY_BOX_DIMENSION, y, PARTY_BOX_DIMENSION, PARTY_BOX_DIMENSION, LiteralText("PartyMember")) {

    private val texture: Identifier = when(this.index) {
        0 -> PARTY_START_TEXTURE
        5 -> PARTY_SIX_TEXTURE
        this.partySize - 1 -> PARTY_END_TEXTURE
        else -> PARTY_SURROUNDED_TEXTURE
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        blitk(
            matrixStack = matrices,
            texture = this.texture,
            x = this.x, y = this.y,
            width = PARTY_BOX_DIMENSION, height = PARTY_BOX_DIMENSION
        )
        // Render current slot as selected if needed
        if (this.isClientPartyMember && this.summary.currentPokemon.uuid == this.pokemon?.uuid) {
            blitk(
                matrixStack = matrices,
                texture = SELECTED_TEXTURE,
                x = this.x + 2, y = this.y + 2,
                width = SELECTED_WIDTH, height = SELECTED_HEIGHT
            )
        }
        if (this.pokemon == null) {
            return
        }
        matrices.push()
        val minecraft = MinecraftClient.getInstance()
        RenderSystem.enableScissor(
            this.scaleIt(this.x + 2),
            minecraft.window.height - this.scaleIt(this.y + PORTRAIT_DIMENSIONS + 2),
            this.scaleIt(PORTRAIT_DIMENSIONS - 1),
            this.scaleIt(PORTRAIT_DIMENSIONS)
        )
//        blitk(matrices, CobbledResources.RED, 0, 0, 1000, 1000)
        matrices.translate(this.x + (PORTRAIT_DIMENSIONS / 2.0) + 2, this.y + 0.0, 0.0)
        matrices.scale(2.5F, 2.5F, 1F)
        drawProfilePokemon(
            pokemon = this.pokemon,
            matrixStack = matrices,
            rotation = Quaternion.fromEulerXyzDegrees(Vec3f(13F, 35F, 0F)),
            state = null,
            scale = 6F
        )
        RenderSystem.disableScissor()
        matrices.pop()
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (this.summary.currentPokemon.uuid != this.pokemon?.uuid && this.isValidClick(mouseX, mouseY, button)) {
            this.summary.switchSelection(this.index)
            return true
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    private fun isValidClick(mouseX: Double, mouseY: Double, button: Int): Boolean = button == 0
        && mouseX.toInt() in this.x..(this.x + this.width)
        && mouseY.toInt() in this.y..(this.y + this.height)

    companion object {

        // Party slot
        private const val PARTY_BOX_DIMENSION = 30
        private val PARTY_START_TEXTURE = cobbledResource("ui/summary/summary_party_1.png")
        private val PARTY_END_TEXTURE = cobbledResource("ui/summary/summary_party_2.png")
        private val PARTY_SURROUNDED_TEXTURE = cobbledResource("ui/summary/summary_party_2-5.png")
        private val PARTY_SIX_TEXTURE = cobbledResource("ui/summary/summary_party_6.png")

        // Selected member
        private val SELECTED_TEXTURE = cobbledResource("ui/summary/summary_overlay_party.png")
        private const val SELECTED_WIDTH = PARTY_BOX_DIMENSION - 4
        private const val SELECTED_HEIGHT = PARTY_BOX_DIMENSION - 4

        // Portrait
        private const val PORTRAIT_DIMENSIONS = 27

    }

}