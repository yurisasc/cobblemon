package com.cablemc.pokemoncobbled.common.client.gui.summary.widgets

import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.gui.drawProfilePokemon
import com.cablemc.pokemoncobbled.common.client.gui.summary.Summary
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.MatrixStack
import com.mojang.math.Quaternion
import com.mojang.math.Vec3f
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.LiteralText
import java.security.InvalidParameterException

class PartyWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    val isParty: Boolean,
    val summary: Summary,
    private val pokemonList: List<Pokemon?>
) : SoundlessWidget(pX - PARTY_BOX_WIDTH.toInt(), pY + 8, pWidth, pHeight, LiteralText("PartyOverlay")) {

    companion object {
        private val partyResourceStart = cobbledResource("ui/summary/summary_party_1.png")
        private val partyResourceEnd = cobbledResource("ui/summary/summary_party_2.png")
        private val partyResourceSurrounded = cobbledResource("ui/summary/summary_party_2-5.png")
        private val partyResourceSix = cobbledResource("ui/summary/summary_party_6.png")
        private val summaryOverlayParty = cobbledResource("ui/summary/summary_overlay_party.png")

        private const val PARTY_BOX_WIDTH = 32.0F
        private const val PARTY_BOX_HEIGHT = 32F
        private const val PARTY_BOX_HEIGHT_DIFF = 29F
        private const val PARTY_PORTRAIT_WIDTH = 27
        private const val PARTY_PORTRAIT_HEIGHT = 27
    }

    private val partySize = pokemonList.size
    private var iMax = partySize - 1
    private val minecraft = MinecraftClient.getInstance()

    init {
        if (partySize > 6 || partySize < 1)
            throw InvalidParameterException("Invalid party size")
    }

    override fun render(pMatrixStack: MatrixStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        val highlightSlot = if (isParty) PokemonCobbledClient.storage.myParty.indexOf(summary.currentPokemon) else -1

        fun renderSelected(x: Number, y: Number) {
            blitk(
                poseStack = pMatrixStack,
                texture = summaryOverlayParty,
                x = x.toFloat() + 2, y = y.toFloat() + 2,
                width = PARTY_BOX_WIDTH - 4, height = PARTY_BOX_HEIGHT - 5
            )
        }

        if (partySize > 1) {
            blitk(
                poseStack = pMatrixStack,
                texture = partyResourceStart,
                x = x, y = y,
                width = PARTY_BOX_WIDTH, height = PARTY_BOX_HEIGHT
            )
            if (highlightSlot == 0) {
                renderSelected(x, y)
            }
            for (i in 1 until iMax) {
                val y = y + i * PARTY_BOX_HEIGHT_DIFF + i * -0.5
                blitk(
                    poseStack = pMatrixStack,
                    texture = partyResourceSurrounded,
                    x = x, y = y,
                    width = PARTY_BOX_WIDTH, height = PARTY_BOX_HEIGHT
                )

                if (highlightSlot == i) {
                    renderSelected(x, y)
                }
            }
            blitk(
                poseStack = pMatrixStack,
                texture = if (iMax == 5) partyResourceSix else partyResourceEnd,
                x = x, y = y + iMax * PARTY_BOX_HEIGHT_DIFF - 3F,
                width = PARTY_BOX_WIDTH, height = PARTY_BOX_HEIGHT
            )
            if (highlightSlot == iMax) {
                renderSelected(x, y + iMax * PARTY_BOX_HEIGHT_DIFF - 3F)
            }

            renderPKM(pMatrixStack)
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button != 0) {
            return false
        }

        if (mouseX > x && mouseX < x + PARTY_BOX_WIDTH && mouseY > y && mouseY < y + (partySize - 1) * PARTY_BOX_HEIGHT_DIFF - 3F) {
            val diff = mouseY - y
            val clickedIndex = (diff / PARTY_BOX_HEIGHT_DIFF).toInt()
            val newPokemon = pokemonList[clickedIndex]
            if (newPokemon != null) {
                summary.switchSelection(clickedIndex)
            }
            return true
        }

        return false
    }

    private fun renderPKM(poseStack: MatrixStack) {
        pokemonList.forEachIndexed { index, pokemon ->
            pokemon?.run {
                poseStack.push()
                RenderSystem.enableScissor(
                    ((x + 2.5) * minecraft.window.guiScale).toInt(),
                    (minecraft.window.height - (y * minecraft.window.guiScale) - (index + 1) * (PARTY_PORTRAIT_HEIGHT + 1.4) * minecraft.window.guiScale).toInt(),
                    ((PARTY_PORTRAIT_WIDTH) * minecraft.window.guiScale).toInt(),
                    ((PARTY_PORTRAIT_HEIGHT - 1) * minecraft.window.guiScale).toInt()
                )

//                blitk(
//                    x = 0,
//                    y = 0,
//                    width = 1000,
//                    height = 1000,
//                    texture = CobbledResources.RED,
//                    poseStack = poseStack,
//                    alpha = 0.5
//                )

                poseStack.translate((x + width / 21.0), y + index * PARTY_BOX_HEIGHT_DIFF.toDouble(), 0.0)
                poseStack.scale(2.5F, 2.5F, 1F)

                drawProfilePokemon(
                    pokemon = this,
                    poseStack = poseStack,
                    rotation = Quaternion.fromXYZDegrees(Vec3f(13F, 35F, 0F)),
                    scale = 6F
                )

                RenderSystem.disableScissor()

                poseStack.pop()
            }
        }
    }
}