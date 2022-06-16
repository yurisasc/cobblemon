package com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.pages.info

import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.SoundlessWidget
import com.cablemc.pokemoncobbled.common.client.gui.summary.SummaryButton
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.pages.info.evolution.EvolutionListWidget
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.asTranslated
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText

class InfoWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    private val pokemon: Pokemon
): SoundlessWidget(pX, pY, pWidth, pHeight, LiteralText("InfoWidget")) {

    private val evolutionListWidget = EvolutionListWidget(
        pX = x + 175, pY = y + 3,
        pWidth = 120/* EXTRA_MENU_WIDTH */, pHeight = 195/* EXTRA_MENU_HEIGHT */,
        infoWidget = this,
        pokemon = this.pokemon
    ).also { widget -> this.addWidget(widget) }
    private val evolutionListButton = SummaryButton(
        x + 10, y + 30,
        SummaryButton.BUTTON_WIDTH, SummaryButton.BUTTON_HEIGHT,
        0, 0, 4,
        clickAction = { this.evolutionListWidget.render = true },
        text = "pokemoncobbled.ui.evolve".asTranslated(),
        renderRequirement = { PokemonCobbledClient.storage.hasPendingEvolutions(this.pokemon) },
        clickRequirement = { !this.evolutionListWidget.render }
    ).also { button -> this.addWidget(button) }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        // Rendering Info Texture
        RenderSystem.setShaderTexture(0, infoBaseResource)
        RenderSystem.enableDepthTest()
        drawTexture(matrices, x, y, 0F, 0F, width, height, width, height)
        this.evolutionListButton.render(matrices, mouseX, mouseY, delta)
        this.evolutionListWidget.render(matrices, mouseX, mouseY, delta)
    }

    companion object {

        private val infoBaseResource = cobbledResource("ui/summary/summary_info.png")

        private const val EXTRA_MENU_WIDTH = 470
        private const val EXTRA_MENU_HEIGHT = 790

    }

}