package com.cablemc.pokemoncobbled.client.gui.summary

import com.cablemc.pokemoncobbled.client.gui.blitk
import com.cablemc.pokemoncobbled.client.gui.summary.mock.DamageCategories
import com.cablemc.pokemoncobbled.client.gui.summary.mock.PokemonMove
import com.cablemc.pokemoncobbled.client.gui.summary.mock.PokemonTypes
import com.cablemc.pokemoncobbled.client.gui.summary.widgets.PartyWidget
import com.cablemc.pokemoncobbled.client.gui.summary.widgets.pages.info.InfoWidget
import com.cablemc.pokemoncobbled.client.gui.summary.widgets.pages.stats.StatWidget
import com.cablemc.pokemoncobbled.client.gui.summary.widgets.pages.SummarySwitchButton
import com.cablemc.pokemoncobbled.client.gui.summary.widgets.pages.moves.MovesWidget
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.resources.ResourceLocation

class Summary: Screen(TranslatableComponent("pokemoncobbled.ui.summary.title")) {

    companion object {
        // Size of UI at Scale 1
        private const val BASE_WIDTH = 325
        private const val BASE_HEIGHT = 200

        // Page Numbers
        private const val INFO = 0
        private const val MOVES = 1
        private const val STATS = 2

        // Resources
        private val baseResource = ResourceLocation(PokemonCobbled.MODID, "ui/summary/summary_base.png")
        private val displayBackgroundResource = ResourceLocation(PokemonCobbled.MODID, "ui/summary/summary_display.png")
        private val exitButtonResource = ResourceLocation(PokemonCobbled.MODID, "ui/summary/summary_overlay_exit.png")
    }

    /**
     * The current page being displayed
     */
    private lateinit var currentPage: AbstractWidget

    /**
     * Initializes the Summary Screen
     */
    override fun init() {
        super.init()

        val x = (width - BASE_WIDTH) / 2
        val y = (height - BASE_HEIGHT) / 2

        // Currently always starting with the MovesWidget
        currentPage = MovesWidget(x, y, BASE_WIDTH, BASE_HEIGHT, this)

        // Add Buttons to change Pages - START
        addRenderableWidget(SummarySwitchButton(x + 3, y + 4, 55, 17, TranslatableComponent("pokemoncobbled.ui.info")) {
            switchTo(INFO)
        })
        addRenderableWidget(SummarySwitchButton(x + 62, y + 4, 55, 17, TranslatableComponent("pokemoncobbled.ui.moves")) {
            switchTo(MOVES)
        })
        addRenderableWidget(SummarySwitchButton(x + 121, y + 4, 55, 17, TranslatableComponent("pokemoncobbled.ui.stats")) {
            switchTo(STATS)
        })
        // Add Buttons to change Pages - END

        // Add Exit Button
        addRenderableWidget(ExitButton(x + 296, y + 6, 25, 14, 0, 0, 0, exitButtonResource, BASE_WIDTH, BASE_HEIGHT) {
            Minecraft.getInstance().setScreen(null)
        })

        // Add Party
        addRenderableWidget(PartyWidget(x, y, BASE_WIDTH, BASE_HEIGHT, 3))

        // Add CurrentPage
        addRenderableWidget(currentPage)
    }

    /**
     * Switches to the given Page
     */
    private fun switchTo(page: Int) {
        removeWidget(currentPage)
        when (page) {
            INFO -> {
                currentPage = InfoWidget((width - BASE_WIDTH) / 2, (height - BASE_HEIGHT) / 2, BASE_WIDTH, BASE_HEIGHT)
            }
            MOVES -> {
                currentPage = MovesWidget((width - BASE_WIDTH) / 2, (height - BASE_HEIGHT) / 2, BASE_WIDTH, BASE_HEIGHT, this)
            }
            STATS -> {
                currentPage = StatWidget((width - BASE_WIDTH) / 2, (height - BASE_HEIGHT) / 2, BASE_WIDTH, BASE_HEIGHT)
            }
        }
        addRenderableWidget(currentPage)
    }

    /**
     * Temporary Method for Mock-Pokemon Moves - Maybe give the MoveWidget the Pokémon it should display or let this return the actual moves
     * (Qu: I think the first one is better due to changing the order of the moves?)
     */
    fun pokemonMoves(): Array<PokemonMove?> {
        return arrayOf(
            PokemonMove("Flare Blitz", PokemonTypes.FIRE, DamageCategories.PHYSICAL, "Does fire stuff1", 100.0, 120.0, 10.0, 3, 10),
            PokemonMove("Dragon Pulse", PokemonTypes.DRAGON, DamageCategories.PHYSICAL, "Does fire stuff2", 100.0, 100.0, 10.0, 10, 10),
            PokemonMove("Scald", PokemonTypes.WATER, DamageCategories.SPECIAL, "Does fire stuff3", 100.0, 80.0, 10.0, 5, 10),
            PokemonMove("Magical Leaf", PokemonTypes.GRASS, DamageCategories.SPECIAL, "Does fire stuff4", 100.0, 60.0, 10.0, 9, 10)
        )
    }

    override fun render(pMatrixStack: PoseStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        renderBackground(pMatrixStack)

        // Render Base Resource
        blitk(pMatrixStack, baseResource,
            (width - BASE_WIDTH) / 2, (height - BASE_HEIGHT) / 2,
            BASE_HEIGHT, BASE_WIDTH
        )

        // Render all added Widgets
        super.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks)
    }

    /**
     * Whether this Screen should pause the Game in SinglePlayer
     */
    override fun isPauseScreen(): Boolean {
        return false
    }
}