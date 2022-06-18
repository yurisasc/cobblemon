package com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.pages.info.evolution

import com.cablemc.pokemoncobbled.common.api.gui.ColourLibrary
import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.api.gui.drawText
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.EvolutionDisplay
import com.cablemc.pokemoncobbled.common.client.CobbledResources
import com.cablemc.pokemoncobbled.common.client.gui.summary.SummaryButton
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.pages.moves.change.MoveSwitchPane
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.asTranslated
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.MutableText
import net.minecraft.text.Text

class EvolutionListWidget(private val pokemon: Pokemon) : AlwaysSelectedEntryListWidget<EvolutionListWidget.EvolutionOption>(
    MinecraftClient.getInstance(),
    MoveSwitchPane.PANE_WIDTH,
    MoveSwitchPane.PANE_HEIGHT,
    1,
    1 + MoveSwitchPane.PANE_HEIGHT,
    MoveSwitchPane.MOVE_HEIGHT
) {

    var render = false

    private val scaledX: Int
        get() = client.window.scaledWidth / 2 + 13
    private val scaledY: Int
        get() = client.window.scaledHeight / 2 - 75

    init {
        updateSize(MoveSwitchPane.PANE_WIDTH, MoveSwitchPane.PANE_HEIGHT - 6, this.scaledY, this.scaledY + MoveSwitchPane.PANE_HEIGHT - 4)
        setLeftPos(this.scaledX)
        setRenderHorizontalShadows(false)
        setRenderBackground(false)
        setRenderSelection(false)
        this.pokemon.evolutionProxy.client().forEach { evolutionDisplay ->
            this.addEntry(EvolutionOption(this.pokemon, evolutionDisplay))
        }
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        if (this.render) {
            blitk(
                matrixStack = matrices,
                texture = OVERLAY_RESOURCE,
                x = left,
                y = top - 4,
                height = MoveSwitchPane.PANE_HEIGHT,
                width = MoveSwitchPane.PANE_WIDTH
            )
            super.render(matrices, mouseX, mouseY, delta)
        }
    }

    class EvolutionOption(private val pokemon: Pokemon, private val evolution: EvolutionDisplay) : AlwaysSelectedEntryListWidget.Entry<EvolutionOption>() {

        private var confirmButton: SummaryButton? = null

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
            blitk(
                matrixStack = matrices,
                texture = ENTRY_RESOURCE,
                x = x + ENTRY_X_OFFSET, y = y,
                width = ENTRY_WIDTH, height = ENTRY_HEIGHT,
            )
            matrices.push()
            val textScale = .65F
            matrices.scale(textScale, textScale, 1F)
            drawText(
                poseStack = matrices,
                font = CobbledResources.NOTO_SANS_BOLD,
                text = this.displayName(),
                x = (x + POKEMON_NAME_X_OFFSET) / textScale, y = (y + POKEMON_NAME_Y_OFFSET) / textScale,
                centered = true,
                colour = ColourLibrary.WHITE, shadow = false
            )
            matrices.pop()
            this.createOrGetConfirmationButton(x + BUTTON_X_OFFSET, y + BUTTON_Y_OFFSET).render(matrices, mouseX, mouseY, tickDelta)
        }

        // ToDo narration should return Undiscovered or something among those lines if not registered in the Pokédex just so it makes a bit more sense coming from TTS
        override fun getNarration(): Text = this.displayName()

        override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
            if (this.confirmButton?.isHovered == true) {
                this.confirmButton?.onPress()
                return true
            }
            return false
        }

        private fun displayName(): MutableText {
            // ToDo return ??? if not registered as caught in Pokédex
            return this.evolution.species.translatedName
        }

        private fun createOrGetConfirmationButton(x: Int, y: Int): ClickableWidget {
            if (this.confirmButton != null) {
                return this.confirmButton!!
            }
            this.confirmButton = SummaryButton(
                x, y,
                BUTTON_WIDTH, BUTTON_HEIGHT,
                0, 0, 0,
                clickAction = {
                    this.pokemon.evolutionProxy.client().start(this.evolution)
                    println("Clicked the button")
                },
                text = "pokemoncobbled.ui.evolve".asTranslated()
            )
            return this.confirmButton!!
        }

        companion object {

            // Entry
            private val ENTRY_RESOURCE = cobbledResource("ui/summary/summary_info_evolve_slot.png")
            private const val ENTRY_WIDTH = 100
            private const val ENTRY_HEIGHT = 35
            private const val ENTRY_X_OFFSET = 55

            // Text overlay
            private const val POKEMON_NAME_X_OFFSET = ENTRY_X_OFFSET + 55
            private const val POKEMON_NAME_Y_OFFSET = 2

            // Confirmation button
            private const val BUTTON_WIDTH = 30
            private const val BUTTON_HEIGHT = 12
            private const val BUTTON_X_OFFSET = ENTRY_X_OFFSET + 68
            private const val BUTTON_Y_OFFSET = 18

        }

    }

    companion object {

        private val OVERLAY_RESOURCE = cobbledResource("ui/summary/summary_moves_change.png")

    }

}