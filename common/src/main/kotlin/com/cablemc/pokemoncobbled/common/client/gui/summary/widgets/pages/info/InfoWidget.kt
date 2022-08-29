package com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.pages.info

import com.cablemc.pokemoncobbled.common.client.gui.summary.SummaryButton
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.SoundlessWidget
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.pages.info.evolution.EvolutionListScrollPane
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.cablemc.pokemoncobbled.common.util.lang
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText

class InfoWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    private val pokemon: Pokemon
): SoundlessWidget(pX, pY, pWidth, pHeight, LiteralText("InfoWidget")) {

    private val evolutionListWidget = EvolutionListScrollPane(this.pokemon).also { widget -> this.addWidget(widget) }

    private val evolutionListButton = SummaryButton(
        buttonX = x + 10F,
        buttonY = y + 30F,
        buttonWidth = SummaryButton.BUTTON_WIDTH,
        buttonHeight = SummaryButton.BUTTON_HEIGHT,
        clickAction = { this.evolutionListWidget.render = true },
        text = lang("ui.evolve"),
        renderRequirement = { this.pokemon.evolutionProxy.client().isNotEmpty() },
        clickRequirement = { this.pokemon.evolutionProxy.client().isNotEmpty() && !this.evolutionListWidget.render }
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

    }

}