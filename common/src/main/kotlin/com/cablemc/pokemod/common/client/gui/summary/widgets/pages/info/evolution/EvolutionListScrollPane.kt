/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.gui.summary.widgets.pages.info.evolution

import com.cablemc.pokemod.common.api.gui.ColourLibrary
import com.cablemc.pokemod.common.api.gui.blitk
import com.cablemc.pokemod.common.api.gui.drawPortraitPokemon
import com.cablemc.pokemod.common.api.pokemon.evolution.EvolutionDisplay
import com.cablemc.pokemod.common.api.types.ElementalType
import com.cablemc.pokemod.common.client.gui.summary.SummaryButton
import com.cablemc.pokemod.common.client.gui.summary.widgets.ModelWidget
import com.cablemc.pokemod.common.client.gui.summary.widgets.common.ModelSectionScrollPane
import com.cablemc.pokemod.common.client.render.drawScaledText
import com.cablemc.pokemod.common.pokemon.Pokemon
import com.cablemc.pokemod.common.util.lang
import com.cablemc.pokemod.common.util.pokemodResource
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.MutableText
import net.minecraft.text.Text

/**
 * Displays the different possible pending evolutions in the [Pokemon.evolutionProxy].
 *
 * @property pokemon The currently selected [Pokemon] from the party list.
 *
 * @author Licious
 * @since June 15th, 2022
 */
class EvolutionListScrollPane(private val pokemon: Pokemon) : ModelSectionScrollPane<EvolutionListScrollPane.EvolutionOption>(
    overlayTexture = OVERLAY_RESOURCE,
    topOffset = 0,
    bottomOffset = 0,
    entryWidth = ENTRY_WIDTH,
    entryHeight = ENTRY_HEIGHT
) {

    var render = false

    override fun createEntries() = this.pokemon.evolutionProxy.client().map { EvolutionOption(this.pokemon, it) }

    override fun render(poseStack: MatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        if (this.render) {
            this.renderPropositionText(poseStack, this.left, this.top)
            super.render(poseStack, mouseX, mouseY, partialTicks)
        }
    }

    private fun renderPropositionText(matrices: MatrixStack, x: Int, y: Int) {
        matrices.push()
        matrices.scale(PROPOSITION_TEXT_SCALE, PROPOSITION_TEXT_SCALE, 1F)
        val text = lang("ui.evolve_offer")
        val compressedText = this.client.textRenderer.wrapLines(text, PROPOSITION_TEXT_MAX_WIDTH)
        var current = 0
        compressedText.forEach { line ->
            this.client.textRenderer.draw(
                matrices,
                line,
                (x + PROPOSITION_X_OFFSET) / PROPOSITION_TEXT_SCALE,
                (y + PROPOSITION_Y_OFFSET + current) / PROPOSITION_TEXT_SCALE,
                ColourLibrary.WHITE
            )
            current += PROPOSITION_LINE_SPACE
        }
        matrices.pop()
    }

    inner class EvolutionOption(private val pokemon: Pokemon, private val evolution: EvolutionDisplay) : Entry<EvolutionOption>() {
        val form = evolution.species.getForm(evolution.aspects)

        private var evolveButton: SummaryButton = SummaryButton(
            buttonX = 0F + BUTTON_X_OFFSET,
            buttonY = 0F + BUTTON_Y_OFFSET,
            buttonWidth = BUTTON_WIDTH,
            buttonHeight = BUTTON_HEIGHT,
            resource = BUTTON_RESOURCE,
            clickAction = { this.acceptAndClose() },
            text = lang("ui.evolve"),
            buttonScale = BUTTON_SCALE
        )

        fun scaleIt(value: Number) = (MinecraftClient.getInstance().window.scaleFactor * value.toFloat()).toInt()

        override fun render(
            matrices: MatrixStack,
            index: Int,
            y: Int,
            x: Int,
            entryWidth: Int,
            entryHeight: Int,
            mouseX: Int,
            mouseY: Int,
            hovered: Boolean,
            tickDelta: Float
        ) {
            // We render this first so it fits nicely behind the entry itself
            this.renderModelUnderlay(matrices, x, y)
            RenderSystem.enableScissor(
                scaleIt(x + MODEL_UNDERLAY_X_OFFSET),
                MinecraftClient.getInstance().window.height - scaleIt(y - 1 + MODEL_UNDERLAY_HEIGHT),
                scaleIt(MODEL_UNDERLAY_WIDTH),
                scaleIt(MODEL_UNDERLAY_HEIGHT - 4)
            )
            this.renderModelPortrait(matrices, x + MODEL_UNDERLAY_WIDTH / 2 + MODEL_UNDERLAY_X_OFFSET, y + 4)
            RenderSystem.disableScissor()
            val isDualType = form.secondaryType != null
            // We want to offset the entries a bit for them to not collide with the scroll bar
            val entryTexture = if (isDualType) DUAL_TYPE_ENTRY_RESOURCE else SINGLE_TYPE_ENTRY_RESOURCE
            blitk(
                matrixStack = matrices,
                texture = entryTexture,
                x = (x + ENTRY_X_OFFSET), y = y,
                width = entryWidth, height = entryHeight,
            )
            this.renderPreviewName(matrices, x, y)
            this.renderButton(matrices, mouseX, mouseY, tickDelta, x, y)
            this.renderTyping(matrices, x, y, isDualType)
        }

        // ToDo narration should return Undiscovered or something among those lines if not registered in the Pokédex just so it makes a bit more sense coming from TTS
        override fun getNarration(): Text = this.displayName()

        override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
            if (this.evolveButton.isHovered) {
                this.evolveButton.onPress()
                return true
            }
            return false
        }

        private fun displayName(): MutableText {
            // ToDo return ??? if not registered as caught in Pokédex
            return this.evolution.species.translatedName
        }

        private fun renderModelUnderlay(matrices: MatrixStack, x: Int, y: Int) {
            blitk(
                matrixStack = matrices,
                texture = MODEL_UNDERLAY_RESOURCE,
                x = x + MODEL_UNDERLAY_X_OFFSET, y = y,
                width = MODEL_UNDERLAY_WIDTH, height = MODEL_UNDERLAY_HEIGHT,
            )
        }

        private fun renderPreviewName(matrices: MatrixStack, x: Int, y: Int) {
            drawScaledText(
                matrixStack = matrices,
                text = displayName(),
                x = x.toFloat() + POKEMON_NAME_X_OFFSET, y = y.toFloat() + POKEMON_NAME_Y_OFFSET,
                scale = POKEMON_NAME_SCALE,
                colour = ColourLibrary.WHITE,
                maxCharacterWidth = POKEMON_NAME_MAX_WIDTH
            )
        }

        private fun renderButton(matrices: MatrixStack, mouseX: Int, mouseY: Int, tickDelta: Float, x: Int, y: Int) {
            evolveButton.setPosFloat(x + BUTTON_X_OFFSET, y + BUTTON_Y_OFFSET)
            evolveButton.render(matrices, mouseX, mouseY, tickDelta)
        }

        private fun acceptAndClose() {
            ModelWidget.render = true
            MinecraftClient.getInstance().player?.closeScreen()
            this.pokemon.evolutionProxy.client().start(this.evolution)
        }

        private fun renderTyping(matrices: MatrixStack, x: Int, y: Int, isDualType: Boolean) {
            matrices.push()
            matrices.scale(TYPE_ICON_SCALE, TYPE_ICON_SCALE, 1F)
            if (isDualType) {
                this.renderTypeIcon(form.secondaryType!!, matrices, x + DUAL_TYPE_ICON_X_OFFSET_2, y + DUAL_TYPE_ICON_Y_OFFSET_2)
                this.renderTypeIcon(form.primaryType, matrices, x + DUAL_TYPE_ICON_X_OFFSET_1, y + DUAL_TYPE_ICON_Y_OFFSET_1)
            }
            else {
                this.renderTypeIcon(form.primaryType, matrices, x + SINGLE_TYPE_ICON_X_OFFSET, y + SINGLE_TYPE_ICON_Y_OFFSET)
            }
            matrices.pop()
        }

        private fun renderTypeIcon(type: ElementalType, matrices: MatrixStack, x: Number, y: Number) {
            blitk(
                matrixStack = matrices,
                texture = TYPE_CHART_RESOURCE,
                x = x.toFloat() / TYPE_ICON_SCALE, y = y.toFloat() / TYPE_ICON_SCALE,
                width = TYPE_ICON_WIDTH, height = TYPE_ICON_HEIGHT,
                uOffset = TYPE_ICON_WIDTH * type.textureXMultiplier.toFloat(),
                textureWidth = TYPE_ICON_WIDTH * 18
            )
        }

        private fun renderModelPortrait(matrices: MatrixStack, x: Int, y: Int) {
            matrices.push()
            matrices.translate(
                x.toDouble() - 4,
                y.toDouble() - 4,
                0.0
            )
            drawPortraitPokemon(
                species = this.evolution.species,
                aspects = this.evolution.aspects,
                matrixStack = matrices,
                scale = MODEL_SCALE,
                state = null
            )
            matrices.pop()
        }

    }

    companion object {

        private val OVERLAY_RESOURCE = pokemodResource("ui/summary/summary_moves_change.png")

        // Entry
        private val SINGLE_TYPE_ENTRY_RESOURCE = pokemodResource("ui/summary/summary_info_evolve_slot1.png")
        private val DUAL_TYPE_ENTRY_RESOURCE = pokemodResource("ui/summary/summary_info_evolve_slot2.png")
        private const val ENTRY_WIDTH = 99
        private const val ENTRY_HEIGHT = 39
        private const val ENTRY_X_OFFSET = -7

        // Proposition text
        private const val PROPOSITION_TEXT_MAX_WIDTH = 90
        private const val PROPOSITION_TEXT_SCALE = .75F
        private const val PROPOSITION_X_OFFSET = 5
        private const val PROPOSITION_Y_OFFSET = -20
        private const val PROPOSITION_LINE_SPACE = 9

        // Pokémon name text
        private const val POKEMON_NAME_MAX_WIDTH = ENTRY_WIDTH - 5
        private const val POKEMON_NAME_SCALE = 0.75F
        private const val POKEMON_NAME_X_OFFSET = 28
        private const val POKEMON_NAME_Y_OFFSET = 5

        // Confirmation button
        private val BUTTON_RESOURCE = pokemodResource("ui/summary/summary_info_evolve_slot_button.png")
        private const val BUTTON_SCALE = 1F
        private const val BUTTON_WIDTH_TO_HEIGHT = 108F / 40F
        private const val BUTTON_WIDTH = 27F
        private const val BUTTON_HEIGHT = BUTTON_WIDTH / BUTTON_WIDTH_TO_HEIGHT
        private const val BUTTON_X_OFFSET = 62.25F
        private const val BUTTON_Y_OFFSET = 18.75F

        // Type preview
        private val TYPE_CHART_RESOURCE = pokemodResource("ui/types.png")
        private const val TYPE_ICON_WIDTH = 76F
        private const val TYPE_ICON_HEIGHT = 76F
        private const val TYPE_ICON_SCALE = .245F
        private const val SINGLE_TYPE_ICON_X_OFFSET = 33.8F
        private const val SINGLE_TYPE_ICON_Y_OFFSET = 14.5F
        private const val DUAL_TYPE_ICON_X_OFFSET_1 = 26.0F
        private const val DUAL_TYPE_ICON_X_OFFSET_2 = 41.5F
        private const val DUAL_TYPE_ICON_Y_OFFSET_1 = 14.5F
        private const val DUAL_TYPE_ICON_Y_OFFSET_2 = 14.5F

        // Model preview
        private val MODEL_UNDERLAY_RESOURCE = pokemodResource("ui/summary/summary_info_evolve_underlay.png")
        private const val MODEL_UNDERLAY_WIDTH = 34
        private const val MODEL_UNDERLAY_HEIGHT = MODEL_UNDERLAY_WIDTH
        private const val MODEL_UNDERLAY_X_OFFSET = -4
        private const val MODEL_SCALE = 18F

    }

}