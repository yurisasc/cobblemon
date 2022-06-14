package com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.pages.info

import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.SoundlessWidget
import com.cablemc.pokemoncobbled.common.client.gui.summary.SummaryButton
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.pages.info.evolution.EvolveListWidget
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.asTranslated
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText

class InfoWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    private val pokemon: Pokemon
): SoundlessWidget(pX, pY, pWidth, pHeight, LiteralText("InfoWidget")) {

    private val evolutionListButton = SummaryButton(x + 10, y + 30, SummaryButton.BUTTON_WIDTH, SummaryButton.BUTTON_HEIGHT, 0, 0, 4, { this.openEvolutionList() }, "pokemoncobbled.ui.evolve".asTranslated(), hoverColorRequirement = { btn -> btn.isHovered || this.evolutionListIsOpen })
    private var evolutionListIsOpen = false

    override fun render(pMatrixStack: MatrixStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        // Rendering Info Texture
        RenderSystem.setShaderTexture(0, infoBaseResource)
        RenderSystem.enableDepthTest()
        drawTexture(pMatrixStack, x, y, 0F, 0F, width, height, width, height)
        val evolutionController = PokemonCobbledClient.storage.pendingEvolutionsOf(this.pokemon) ?: return
        if (evolutionController.isNotEmpty()) {
            this.evolutionListButton.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks)
        }
        if (!this.evolutionListIsOpen) {
            return
        }
        val widget = EvolveListWidget(this, evolutionController)
        widget.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks)
    }

    private fun openEvolutionList() {
        this.evolutionListIsOpen = true
    }

    companion object {

        private val infoBaseResource = cobbledResource("ui/summary/summary_info.png")

    }

}