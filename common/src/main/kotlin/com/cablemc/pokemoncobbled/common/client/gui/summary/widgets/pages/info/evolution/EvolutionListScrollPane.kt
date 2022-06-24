package com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.pages.info.evolution

import com.cablemc.pokemoncobbled.common.api.gui.ColourLibrary
import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.api.gui.drawPortraitPokemon
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.EvolutionDisplay
import com.cablemc.pokemoncobbled.common.api.types.ElementalType
import com.cablemc.pokemoncobbled.common.client.CobbledResources
import com.cablemc.pokemoncobbled.common.client.gui.summary.SummaryButton
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.ModelWidget
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.common.ModelSectionScrollPane
import com.cablemc.pokemoncobbled.common.client.render.drawScaledText
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.asTranslated
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget
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

    override fun createEntries(): Collection<EvolutionOption> = this.pokemon.evolutionProxy.client().map { evolutionDisplay ->
        EvolutionOption(this.pokemon, evolutionDisplay)
    }

    override fun render(poseStack: MatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        if (this.render) {
            this.renderPropositionText(poseStack, this.left, this.top)
            super.render(poseStack, mouseX, mouseY, partialTicks)
        }
    }

    private fun renderPropositionText(matrices: MatrixStack, x: Int, y: Int) {
        matrices.push()
        matrices.scale(PROPOSITION_TEXT_SCALE, PROPOSITION_TEXT_SCALE, 1F)
        val text = "pokemoncobbled.ui.evolve_offer".asTranslated().apply { style = style.withFont(CobbledResources.NOTO_SANS_BOLD_SMALL) }
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

    inner class EvolutionOption(private val pokemon: Pokemon, private val evolution: EvolutionDisplay) : AlwaysSelectedEntryListWidget.Entry<EvolutionOption>() {

        private var lastKnownButton: SummaryButton? = null

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
            val isDualType = this.evolution.form.secondaryType != null
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
            this.renderModelPortrait(matrices, x, y)
        }

        // ToDo narration should return Undiscovered or something among those lines if not registered in the Pokédex just so it makes a bit more sense coming from TTS
        override fun getNarration(): Text = this.displayName()

        override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
            if (this.lastKnownButton?.isHovered == true) {
                this.lastKnownButton?.onPress()
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
            val client = MinecraftClient.getInstance()
            val text = this.displayName().apply {
                style = style.withFont(CobbledResources.NOTO_SANS_BOLD)
            }
            val textWidth = client.textRenderer.getWidth(text).toFloat()
            val scaleMultiplier = if (textWidth >= POKEMON_NAME_MAX_WIDTH) POKEMON_NAME_MAX_WIDTH / textWidth else 1F
            val textScale = (POKEMON_NAME_SCALE * scaleMultiplier).coerceAtMost(POKEMON_NAME_SCALE)
            drawScaledText(
                matrixStack = matrices,
                text = text,
                x = x.toFloat() + POKEMON_NAME_X_OFFSET, y = y.toFloat() + POKEMON_NAME_Y_OFFSET + (1 - textScale),
                scaleX = textScale, scaleY = textScale,
                colour = ColourLibrary.WHITE
            )
        }

        private fun renderButton(matrices: MatrixStack, mouseX: Int, mouseY: Int, tickDelta: Float, x: Int, y: Int) {
            SummaryButton(
                x + BUTTON_X_OFFSET, y + BUTTON_Y_OFFSET,
                BUTTON_WIDTH, BUTTON_HEIGHT,
                resource = BUTTON_RESOURCE,
                clickAction = { this.acceptAndClose() },
                text = "pokemoncobbled.ui.evolve".asTranslated(),
                buttonScale = BUTTON_SCALE
            ).also { button ->
                this.lastKnownButton = button
                button.render(matrices, mouseX, mouseY, tickDelta)
            }
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
                this.renderTypeIcon(this.evolution.form.secondaryType!!, matrices, x + DUAL_TYPE_ICON_X_OFFSET_2, y + DUAL_TYPE_ICON_Y_OFFSET_2)
                this.renderTypeIcon(this.evolution.form.primaryType, matrices, x + DUAL_TYPE_ICON_X_OFFSET_1, y + DUAL_TYPE_ICON_Y_OFFSET_1)
            }
            else {
                this.renderTypeIcon(this.evolution.form.primaryType, matrices, x + SINGLE_TYPE_ICON_X_OFFSET, y + SINGLE_TYPE_ICON_Y_OFFSET)
            }
            matrices.pop()
        }

        private fun renderTypeIcon(type: ElementalType, matrices: MatrixStack, x: Int, y: Int) {
            blitk(
                matrixStack = matrices,
                texture = TYPE_CHART_RESOURCE,
                x = x / TYPE_ICON_SCALE, y = y / TYPE_ICON_SCALE,
                width = TYPE_ICON_WIDTH, height = TYPE_ICON_HEIGHT,
                uOffset = TYPE_ICON_WIDTH * type.textureXMultiplier.toFloat(),
                textureWidth = TYPE_ICON_WIDTH * 18
            )
        }

        private fun renderModelPortrait(matrices: MatrixStack, x: Int, y: Int) {
            val evolutionPokemon = Pokemon().apply {
                species = evolution.species
                form = evolution.form
                shiny = evolution.shiny
                gender = evolution.gender
            }
            matrices.translate(
                x.toDouble(),
                y.toDouble(),
                0.0
            )
            drawPortraitPokemon(
                species = evolutionPokemon.species,
                aspects = evolutionPokemon.aspects,
                matrixStack = matrices,
                scale = MODEL_SCALE,
                state = null
            )
        }

    }

    companion object {

        private val OVERLAY_RESOURCE = cobbledResource("ui/summary/summary_moves_change.png")

        // Entry
        private val SINGLE_TYPE_ENTRY_RESOURCE = cobbledResource("ui/summary/summary_info_evolve_slot1.png")
        private val DUAL_TYPE_ENTRY_RESOURCE = cobbledResource("ui/summary/summary_info_evolve_slot2.png")
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
        private const val POKEMON_NAME_SCALE = .55F
        private const val POKEMON_NAME_X_OFFSET = 27
        private const val POKEMON_NAME_Y_OFFSET = 3

        // Confirmation button
        private val BUTTON_RESOURCE = cobbledResource("ui/summary/summary_info_evolve_slot_button.png")
        private const val BUTTON_SCALE = .25F
        private const val BUTTON_WIDTH = 108
        private const val BUTTON_HEIGHT = 40
        private const val BUTTON_X_OFFSET = 62
        private const val BUTTON_Y_OFFSET = 19

        // Type preview
        private val TYPE_CHART_RESOURCE = cobbledResource("ui/types.png")
        private const val TYPE_ICON_WIDTH = 76
        private const val TYPE_ICON_HEIGHT = 76
        private const val TYPE_ICON_SCALE = .23F
        private const val SINGLE_TYPE_ICON_X_OFFSET = 35
        private const val SINGLE_TYPE_ICON_Y_OFFSET = 15
        private const val DUAL_TYPE_ICON_X_OFFSET_1 = 27
        private const val DUAL_TYPE_ICON_X_OFFSET_2 = 42
        private const val DUAL_TYPE_ICON_Y_OFFSET_1 = 15
        private const val DUAL_TYPE_ICON_Y_OFFSET_2 = 15

        // Model preview
        private val MODEL_UNDERLAY_RESOURCE = cobbledResource("ui/summary/summary_info_evolve_underlay.png")
        private const val MODEL_UNDERLAY_WIDTH = 33
        private const val MODEL_UNDERLAY_HEIGHT = MODEL_UNDERLAY_WIDTH
        private const val MODEL_UNDERLAY_X_OFFSET = -4
        private const val MODEL_SCALE = 12.5F

    }

}