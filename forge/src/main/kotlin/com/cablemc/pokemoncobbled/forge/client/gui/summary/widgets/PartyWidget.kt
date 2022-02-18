package com.cablemc.pokemoncobbled.forge.client.gui.summary.widgets

import com.cablemc.pokemoncobbled.forge.client.gui.blitk
import com.cablemc.pokemoncobbled.forge.client.gui.drawProfilePokemon
import com.cablemc.pokemoncobbled.common.entity.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Quaternion
import com.mojang.math.Vector3f
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.TextComponent
import java.security.InvalidParameterException

class PartyWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    private val pokemonList: List<Pokemon?>
) : SoundlessWidget(pX - PARTY_BOX_WIDTH.toInt(), pY + 50, pWidth, pHeight, TextComponent("PartyOverlay")) {

    companion object {
        private val partyResourceMiddle = cobbledResource("ui/summary/summary_party_1.png")
        private val partyResourceEnd = cobbledResource("ui/summary/summary_party_2.png")
        private const val PARTY_BOX_WIDTH = 32.0F
        private const val PARTY_BOX_HEIGHT = 32.5F
        private const val PARTY_BOX_HEIGHT_DIFF = 30.2F
        private const val PARTY_PORTRAIT_WIDTH = 27.5
        private const val PARTY_PORTRAIT_HEIGHT = 27.5
    }

    private val partySize = pokemonList.size
    private var iMax = partySize - 2
    private val minecraft = Minecraft.getInstance()

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
                    x = x, y = y + i * PARTY_BOX_HEIGHT_DIFF + i * -0.5,
                    width = PARTY_BOX_WIDTH, height = PARTY_BOX_HEIGHT
                )
            }
            if (partySize == 6)
                blitk(
                    poseStack = pPoseStack,
                    texture = partyResourceEnd,
                    x = x, y = y + 4 * PARTY_BOX_HEIGHT_DIFF - 2.75F,
                    width = PARTY_BOX_WIDTH, height = 32
                )
            renderPKM(pPoseStack)
        }
    }

    private fun renderPKM(poseStack: PoseStack) {
        pokemonList.forEachIndexed { index, pokemon ->
            pokemon?.run {
                poseStack.pushPose()
                RenderSystem.enableScissor(
                    ((x + 2) * minecraft.window.guiScale).toInt(),
                    (minecraft.window.height - (y * minecraft.window.guiScale) - index * (PARTY_PORTRAIT_HEIGHT + 2.2) * minecraft.window.guiScale).toInt(),
                    (PARTY_PORTRAIT_WIDTH * minecraft.window.guiScale).toInt(),
                    (PARTY_PORTRAIT_HEIGHT * minecraft.window.guiScale).toInt()
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

                poseStack.translate((x + width / 21.0), y - 26.0 + index * 30, -100.0)
                poseStack.scale(2.5F, 2.5F, 2.5F)

                drawProfilePokemon(
                    pokemon = this,
                    poseStack = poseStack,
                    rotation = Quaternion.fromXYZDegrees(Vector3f(13F, 35F, 0F)),
                    scale = 6F
                )

                RenderSystem.disableScissor()

                poseStack.popPose()
            }
        }
    }

    private fun renderPokemonPortraits(poseStack: PoseStack) {
        val scaleIt: (Int) -> Int = { (it * minecraft.window.guiScale).toInt() }
        poseStack.pushPose()

        RenderSystem.viewport(0, 0, minecraft.window.width, minecraft.window.height) // <-- Reset

        pokemonList.forEachIndexed { i, pokemon ->
            pokemon?.run {
//                RenderSystem.viewport(
//                    scaleIt(x + 1), scaleIt(y + 133 - ((i + 1) * 25).toInt()),
//                    scaleIt(PARTY_PORTRAIT_WIDTH), scaleIt(PARTY_PORTRAIT_WIDTH)
//                )
//                blitk(
//                    poseStack = poseStack, texture = cobbledResource("ui/pokenav/test.png"),
//                    x = x + 2, y = y - 26 + (i * 29) + specificOffset(i), width = PARTY_PORTRAIT_WIDTH, height = PARTY_PORTRAIT_HEIGHT
//                )

//                drawPortraitPokemon(
//                    pokemon = this,
//                    poseStack = PoseStack()
//                )
            }
        }

        RenderSystem.viewport(0, 0, minecraft.window.width, minecraft.window.height) // <-- Reset

        poseStack.popPose()
    }

    private fun specificOffset(partyPos: Int): Int {
        when (partyPos) {
            0 -> return 0
            1 -> return 1
            2 -> return 1
            3 -> return 1
        }
        return 0
    }

}