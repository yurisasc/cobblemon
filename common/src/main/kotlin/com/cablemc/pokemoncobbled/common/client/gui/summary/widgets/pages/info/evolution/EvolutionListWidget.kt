package com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.pages.info.evolution

import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.EvolutionDisplay
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.pages.moves.change.MoveSwitchPane
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget
import net.minecraft.client.util.math.MatrixStack
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
            val result = Pokemon().apply {
                species = evolutionDisplay.species
                shiny = evolutionDisplay.shiny
                form = evolutionDisplay.form
                gender = evolutionDisplay.gender
            }
            this.addEntry(EvolutionOption(result, evolutionDisplay))
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
                x = x + X_OFFSET, y = y,
                width = ENTRY_WIDTH, height = ENTRY_HEIGHT,
            )
        }

        override fun getNarration(): Text {
            // ToDo render ??? if not registered as caught in Pokédex
            return this.pokemon.species.translatedName
        }

        companion object {

            private val ENTRY_RESOURCE = cobbledResource("ui/summary/summary_info_evolve_slot.png")
            private const val ENTRY_WIDTH = 100
            private const val ENTRY_HEIGHT = 35
            private const val X_OFFSET = 55

        }

    }

    companion object {

        private val OVERLAY_RESOURCE = cobbledResource("ui/summary/summary_moves_change.png")

    }

}