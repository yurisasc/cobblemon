package com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.pages.info.evolution

import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.EvolutionController
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.EvolutionDisplay
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.ModelWidget
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.pages.info.InfoWidget
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text

class EvolveListWidget(
    private val infoWidget: InfoWidget,
    private val controller: EvolutionController<EvolutionDisplay>
): AlwaysSelectedEntryListWidget<EvolveListWidget.EvolutionEntry>(
    MinecraftClient.getInstance(),
    PANE_WIDTH,
    PANE_HEIGHT,
    1,
    1 + PANE_HEIGHT,
    ENTRY_HEIGHT
) {

    init {
        this.controller.forEach { evolution ->
            val pokemon = Pokemon().apply {
                species = evolution.species
                form = evolution.form
                shiny = evolution.shiny
                gender = evolution.gender
            }
            this.addEntry(EvolutionEntry(this.controller, evolution, pokemon))
        }
    }

    class EvolutionEntry(private val controller: EvolutionController<EvolutionDisplay>, private val evolution: EvolutionDisplay, private val representation: Pokemon): AlwaysSelectedEntryListWidget.Entry<EvolutionEntry>() {

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
            val widget = ModelWidget(
                pX = x, pY = y,
                pWidth = 102, pHeight = 100,
                pokemon = this.representation
            )
            widget.render(matrices, mouseX, mouseY, tickDelta)
        }

        override fun getNarration(): Text {
            // ToDo render ??? instead if Pokémon hasn't been registered to the Pokédex before, pending Pokédex implementation
            return this.representation.species.translatedName
        }

        override fun mouseClicked(d: Double, e: Double, i: Int): Boolean {
            if (isMouseOver(d, e)) {
                /*
                val pokemon = pane.movesWidget.summary.currentPokemon
                val isParty = pokemon in PokemonCobbledClient.storage.myParty
                CobbledNetwork.sendToServer(
                    BenchMovePacket(
                        isParty = isParty,
                        uuid = pokemon.uuid,
                        oldMove = pane.replacedMove.template,
                        newMove = move
                    )
                )
                 */
                return true
            }
            return false
        }

    }

    companion object {

        const val PANE_HEIGHT = 178
        const val ENTRY_HEIGHT = 24
        const val ENTRY_WIDTH = 112
        const val PANE_WIDTH = ENTRY_WIDTH + 5

        private val OVERLAY_RESOURCE = cobbledResource("ui/summary/summary_extra_menu.png")
        private val ENTRY_RESOURCE = cobbledResource("ui/summary/summary_info_evolve_slot.png")
        private val TYPES_RESOURCE = cobbledResource("ui/types.png")

    }

}