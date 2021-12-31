package com.cablemc.pokemoncobbled.client.gui.summary.widgets

import com.cablemc.pokemoncobbled.client.gui.blitk
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.network.chat.TextComponent
import net.minecraft.resources.ResourceLocation
import java.security.InvalidParameterException

class PartyWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    private val partySize: Int
) : SoundlessWidget(pX - PARTY_BOX_WIDTH.toInt(), pY + 50, pWidth, pHeight, TextComponent("PartyOverlay")) {

    companion object {
        private val partyResourceMiddle = ResourceLocation(PokemonCobbled.MODID, "ui/summary/summary_party_1.png")
        private val partyResourceEnd = ResourceLocation(PokemonCobbled.MODID, "ui/summary/summary_party_2.png")
        private const val PARTY_BOX_WIDTH = 30.2F
        private const val PARTY_BOX_HEIGHT = 32.84F
        private const val PARTY_BOX_HEIGHT_DIFF = 30.2F
    }

    private var iMax = partySize - 2

    init {
        if (partySize > 6 || partySize < 1)
            throw InvalidParameterException("Invalid party size")
        if (partySize == 6)
            iMax--
    }

    override fun render(pPoseStack: PoseStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        if (partySize > 1) {
            for (i in 0 .. iMax) {
                blitk(
                    poseStack = pPoseStack,
                    texture = partyResourceMiddle,
                    x = x - 0.1F, y = y + i * PARTY_BOX_HEIGHT_DIFF + specificOffset(i),
                    width =  PARTY_BOX_WIDTH, height = PARTY_BOX_HEIGHT
                )
            }
            if (partySize == 6)
                blitk(
                    poseStack = pPoseStack,
                    texture = partyResourceEnd,
                    x = x - 0.75F, y = y + 4 * PARTY_BOX_HEIGHT_DIFF - 2.75F,
                    width = 30.84F, height = 32
                )
        }
    }

    private fun specificOffset(partyPos: Int): Float {
        when(partyPos) {
            0 -> return 0F
            1 -> return -0.5F
            2 -> return -1F
            3 -> return -1.5F
        }
        return 0F
    }

}