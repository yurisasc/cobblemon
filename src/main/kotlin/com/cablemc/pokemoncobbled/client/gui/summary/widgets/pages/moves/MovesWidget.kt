package com.cablemc.pokemoncobbled.client.gui.summary.widgets.pages.moves

import com.cablemc.pokemoncobbled.client.gui.summary.Summary
import com.cablemc.pokemoncobbled.client.gui.summary.widgets.SoundlessWidget
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.network.chat.TextComponent
import net.minecraft.resources.ResourceLocation

class MovesWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    val summary: Summary
): SoundlessWidget(pX, pY, pWidth, pHeight, TextComponent("MovesWidget")) {

    companion object {
        private const val moveWidth = 121
        private const val moveHeight = 31
        private val movesBaseResource = ResourceLocation(PokemonCobbled.MODID, "ui/summary/summary_moves.png")
        private val moveMoveButtonResource = ResourceLocation(PokemonCobbled.MODID, "ui/summary/summary_moves_overlay_swap.png")
    }

    private var index = -1
    private val moves = summary.pokemonMoves().filterNotNull().map { move ->
        index++
        MoveWidget(x + 19, y + 27 + (moveHeight + 3) * index, moveWidth, moveHeight, move)
    }

    override fun render(pMatrixStack: PoseStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        // Rendering Moves Texture
        RenderSystem.setShaderTexture(0, movesBaseResource)
        RenderSystem.enableDepthTest()
        blit(pMatrixStack, x, y, 0F, 0F, width, height, width, height)
        moves.forEach {
            it.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks)
        }
    }

}