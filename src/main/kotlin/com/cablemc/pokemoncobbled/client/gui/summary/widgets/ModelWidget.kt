package com.cablemc.pokemoncobbled.client.gui.summary.widgets

import com.cablemc.pokemoncobbled.client.gui.drawProfilePokemon
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Quaternion
import com.mojang.math.Vector3f
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.TextComponent

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
    }

    private fun renderPKM(poseStack: PoseStack) {
        poseStack.pushPose()

        RenderSystem.enableScissor(
            (x * minecraft.window.guiScale).toInt(), (minecraft.window.height - (y * minecraft.window.guiScale) - (height * minecraft.window.guiScale)).toInt(),
            (width * minecraft.window.guiScale).toInt(), (height * minecraft.window.guiScale).toInt()
        )

        poseStack.translate((x + width * 0.5), y.toDouble(), -100.0)

        drawProfilePokemon(
            pokemon = pokemon,
            poseStack = poseStack,
            rotation = Quaternion.fromXYZDegrees(rotVec)
        )

        RenderSystem.disableScissor()

        poseStack.popPose()
    }

    override fun onClick(pMouseX: Double, pMouseY: Double) {

    }
}