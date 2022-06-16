package com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.pages.info.evolution

import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.SoundlessWidget
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.pages.info.InfoWidget
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText

class EvolutionListWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    private val infoWidget: InfoWidget,
    private val pokemon: Pokemon
): SoundlessWidget(pX, pY, pWidth, pHeight, LiteralText("InfoWidget")) {

    var render = false

    override fun render(pMatrixStack: MatrixStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        if (!this.render || !PokemonCobbledClient.storage.hasPendingEvolutions(this.pokemon)) {
            return
        }
        RenderSystem.setShaderTexture(0, OVERLAY_RESOURCE)
        RenderSystem.enableDepthTest()
        drawTexture(pMatrixStack, x, y, 0F, 0F, width, height, width, height)
    }

    companion object {

        private val OVERLAY_RESOURCE = cobbledResource("ui/summary/summary_extra_menu.png")
        private val ENTRY_RESOURCE = cobbledResource("ui/summary/summary_info_evolve_slot.png")

    }

}