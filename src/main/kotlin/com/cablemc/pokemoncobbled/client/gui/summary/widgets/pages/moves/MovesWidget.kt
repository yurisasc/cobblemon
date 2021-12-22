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
    summary: Summary
): SoundlessWidget(pX, pY, pWidth, pHeight, TextComponent("MovesWidget")) {

    companion object {
        private const val MOVE_WIDTH = 120
        private const val MOVE_HEIGHT = 30
        private val movesBaseResource = ResourceLocation(PokemonCobbled.MODID, "ui/summary/summary_moves.png")
    }

    private var index = -1
    private val moves = summary.pokemonMoves().filterNotNull().map { move ->
        index++
        MoveWidget(x + 19, y + 27 + (MOVE_HEIGHT + 3) * index, MOVE_WIDTH, MOVE_HEIGHT, move, x + 5, y + 165, width, height, this)
    }.toMutableList().onEach {
        addWidget(it)
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

    private fun updateMoves() {
        moves.forEach {
            it.y = y + 27 + (MOVE_HEIGHT + 3) * moves.indexOf(it)
            it.update()
        }
    }

    fun moveMove(move: MoveWidget, up: Boolean) {
        val movePos = moves.indexOf(move)
        if(moves.size <= movePos || movePos == -1) {
            return
        }
        var targetSlot: Int
        if(up) {
            targetSlot = movePos - 1
            if(targetSlot == -1)
                targetSlot = moves.size - 1
        } else {
            targetSlot = movePos + 1
            if(targetSlot >= moves.size)
                targetSlot = 0
        }
        val temp = moves[targetSlot]
        moves[targetSlot] = moves[movePos]
        moves[movePos] = temp
        updateMoves()
    }

}