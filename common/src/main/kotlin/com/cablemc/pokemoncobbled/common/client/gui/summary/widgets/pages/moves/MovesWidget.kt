package com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.pages.moves

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.gui.summary.Summary
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.ModelWidget
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.SoundlessWidget
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.pages.moves.switch.MoveSwitchPane
import com.cablemc.pokemoncobbled.common.net.messages.server.RequestMoveSwapPacket
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.MatrixStack
import net.minecraft.text.LiteralText

class MovesWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    val summary: Summary
): SoundlessWidget(pX, pY, pWidth, pHeight, LiteralText("MovesWidget")) {
    companion object {
        private const val MOVE_WIDTH = 120
        private const val MOVE_HEIGHT = 30
        private val movesBaseResource = cobbledResource("ui/summary/summary_moves.png")
    }

    var moveSwitchPane: MoveSwitchPane? = null
        set(value) {
            if (field != null) {
                this.removeWidget(field!!)
            }
            field = value
            if (value != null) {
                this.addWidget(value)
            }
        }

    init {
        ModelWidget.render = true
    }

    private var index = -1
    private val moves = summary.currentPokemon.moveSet.getMoves().map { move ->
        index++
        MoveWidget(x + 20, y + 26 + (MOVE_HEIGHT + 3) * index, MOVE_WIDTH, MOVE_HEIGHT, move, x + 6, y + 164, this, index)
    }.toMutableList().onEach {
        addWidget(it)
    }

    override fun render(pMatrixStack: MatrixStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        // Rendering Moves Texture
        RenderSystem.setShaderTexture(0, movesBaseResource)
        RenderSystem.enableDepthTest()
        blit(pMatrixStack, x, y, 0F, 0F, width, height, width, height)
        moves.forEach {
            it.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks)
        }
        moveSwitchPane?.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks)
    }

    private fun updateMoves() {
        moves.forEach {
            it.y = y + 27 + (MOVE_HEIGHT + 3) * moves.indexOf(it)
            it.update()
        }
    }

    fun closeSwitchMoveMenu() {
        moveSwitchPane = null
        ModelWidget.render = true
    }

    fun moveMove(move: MoveWidget, up: Boolean) {
        val movePos = moves.indexOf(move)
        if (moves.size <= movePos || movePos == -1) {
            return
        }
        var targetSlot: Int
        if (up) {
            targetSlot = movePos - 1
            if (targetSlot == -1)
                targetSlot = moves.size - 1
        } else {
            targetSlot = movePos + 1
            if (targetSlot >= moves.size)
                targetSlot = 0
        }
//        moves[targetSlot] = moves[movePos].also {
//            moves[movePos] = moves[targetSlot]
//        }
        //summary.currentPokemon.moveSet.swapMove(targetSlot, movePos)
        CobbledNetwork.sendToServer(
            RequestMoveSwapPacket(
                move1 = movePos,
                move2 = targetSlot,
                slot = PokemonCobbledClient.storage.myParty.getPosition(summary.currentPokemon.uuid)
            )
        )
        //updateMoves()
    }

}