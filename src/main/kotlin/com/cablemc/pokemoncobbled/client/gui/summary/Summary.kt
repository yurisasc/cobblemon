package com.cablemc.pokemoncobbled.client.gui.summary

import com.cablemc.pokemoncobbled.client.gui.blitk
import com.cablemc.pokemoncobbled.client.gui.summary.mock.DamageCategories
import com.cablemc.pokemoncobbled.client.gui.summary.mock.PokemonMove
import com.cablemc.pokemoncobbled.client.gui.summary.mock.PokemonTypes
import com.cablemc.pokemoncobbled.client.gui.summary.widgets.pages.info.InfoWidget
import com.cablemc.pokemoncobbled.client.gui.summary.widgets.pages.stats.StatWidget
import com.cablemc.pokemoncobbled.client.gui.summary.widgets.pages.SummarySwitchButton
import com.cablemc.pokemoncobbled.client.gui.summary.widgets.pages.moves.MoveWidget
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
        // Size of base
        private const val baseWidth = 325
        private const val baseHeight = 200

        // Switch to
        const val INFO = 0
        const val MOVES = 1
        const val STATS = 2
        private val baseResource = ResourceLocation(PokemonCobbled.MODID, "ui/summary/summary_base.png")
        private val displayBackgroundResource = ResourceLocation(PokemonCobbled.MODID, "ui/summary/summary_display.png")
        private val exitButtonResource = ResourceLocation(PokemonCobbled.MODID, "ui/summary/summary_overlay_exit.png")
    }

    private lateinit var currentWidget: AbstractWidget

    override fun init() {
        super.init()

        val x = (width - baseWidth) / 2
        val y = (height - baseHeight) / 2

        currentWidget = MovesWidget(x, y, baseWidth, baseHeight, this)

        addRenderableWidget(SummarySwitchButton(x + 3, y + 4, 55, 17, TranslatableComponent("pokemoncobbled.ui.info")) {
            switchTo(INFO)
        })
        addRenderableWidget(SummarySwitchButton(x + 62, y + 4, 55, 17, TranslatableComponent("pokemoncobbled.ui.moves")) {
            switchTo(MOVES)
        })
        addRenderableWidget(SummarySwitchButton(x + 121, y + 4, 55, 17, TranslatableComponent("pokemoncobbled.ui.stats")) {
            switchTo(STATS)
        })

        addRenderableWidget(ExitButton(x + 296, y + 6, 25, 14, 0, 0, 0, exitButtonResource, 25, 14) {
            Minecraft.getInstance().setScreen(null)
        })
        addRenderableWidget(currentWidget)
    }

    private fun switchTo(page: Int) {
        removeWidget(currentWidget)
        when (page) {
            INFO -> {
                currentWidget = InfoWidget((width - baseWidth) / 2, (height - baseHeight) / 2, baseWidth, baseHeight)
            }
            MOVES -> {
                currentWidget = MovesWidget((width - baseWidth) / 2, (height - baseHeight) / 2, baseWidth, baseHeight, this)
            }
            STATS -> {
                currentWidget = StatWidget((width - baseWidth) / 2, (height - baseHeight) / 2, baseWidth, baseHeight)
            }
        }
        addRenderableWidget(currentWidget)
    }

    fun pokemonMoves(): Array<PokemonMove?> {
        return arrayOf(
            PokemonMove("Flare Blitz", PokemonTypes.FIRE, DamageCategories.PHYSICAL, "Does fire stuff1", 100.0, 120.0, 10.0, 3, 10),
            PokemonMove("Flare Blitz", PokemonTypes.FIRE, DamageCategories.PHYSICAL, "Does fire stuff2", 100.0, 120.0, 10.0, 10, 10),
            PokemonMove("Flare Blitz", PokemonTypes.FIRE, DamageCategories.PHYSICAL, "Does fire stuff3", 100.0, 120.0, 10.0, 5, 10),
            PokemonMove("Flare Blitz", PokemonTypes.FIRE, DamageCategories.PHYSICAL, "Does fire stuff4", 100.0, 120.0, 10.0, 9, 10)
        )
    }

    override fun render(pMatrixStack: PoseStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        renderBackground(pMatrixStack)

        blitk(pMatrixStack, baseResource,
            (width - baseWidth) / 2, (height - baseHeight) / 2,
            baseHeight, baseWidth
        )

        super.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks)
    }

    override fun isPauseScreen(): Boolean {
        return false
    }
}