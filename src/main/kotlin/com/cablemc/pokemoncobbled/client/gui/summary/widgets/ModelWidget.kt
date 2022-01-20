package com.cablemc.pokemoncobbled.client.gui.summary.widgets

import com.cablemc.pokemoncobbled.client.gui.blitk
import com.cablemc.pokemoncobbled.client.gui.drawPokemon
import com.cablemc.pokemoncobbled.client.gui.drawProfilePokemon
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Quaternion
import com.mojang.math.Vector3f
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.TextComponent
import kotlin.math.min

class ModelWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    pokemon: Pokemon
): SoundlessWidget(pX, pY, pWidth, pHeight, TextComponent("Summary - ModelWidget")) {

    var pokemon = pokemon
    private val minecraft = Minecraft.getInstance()
    private var rotVec = Vector3f(13F, 35F, 0F)

    override fun render(pPoseStack: PoseStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        isHovered = pMouseX >= x && pMouseY >= y && pMouseX < x + width && pMouseY < y + height
        renderPKM(pPoseStack)
//        blitk(
//            poseStack = pPoseStack, texture = cobbledResource("ui/pokenav/test.png"),
//            x = x, y = y , width = width, height = height
//        )
    }

    private fun renderPKM(poseStack: PoseStack) {
        poseStack.pushPose()

        val scaleIt: (Int) -> Int = { (it * minecraft.window.guiScale).toInt() }

        RenderSystem.enableScissor(
            scaleIt(x), minecraft.window.height / 2 - scaleIt(y),
            scaleIt(width), scaleIt(height)
        )

        poseStack.translate((x + width * 0.5), (y + height).toDouble() / 2.0, -100.0)
        //poseStack.scale(1F, 1F, 1F)

        drawPokemon(
            pokemon = pokemon,
            poseStack = poseStack
        )

        RenderSystem.disableScissor()

        poseStack.popPose()
    }

    override fun onClick(pMouseX: Double, pMouseY: Double) {

    }
}