package com.cablemc.pokemoncobbled.client.gui.summary.widgets

import com.cablemc.pokemoncobbled.client.gui.drawPokemon
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.TextComponent

class ModelWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    pokemon: Pokemon
): SoundlessWidget(pX + 150, pY + 200, pWidth, pHeight, TextComponent("Summary - ModelWidget")) {

    var pokemon = pokemon
    private val minecraft = Minecraft.getInstance()

    override fun render(pPoseStack: PoseStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        isHovered = pMouseX >= x && pMouseY >= y && pMouseX < x + width && pMouseY < y + height
        renderPKM(pPoseStack)
    }

    private fun renderPKM(poseStack: PoseStack) {
        poseStack.pushPose()

        RenderSystem.viewport(0, 0, minecraft.window.width, minecraft.window.height)

        RenderSystem.viewport(
            x, y,
            width, height
        ) // <-- Coords

        drawPokemon(
            pokemon = pokemon,
            poseStack = poseStack
        ) // <-- Render

        RenderSystem.viewport(0, 0, minecraft.window.width, minecraft.window.height) // <-- Reset

        poseStack.popPose()
    }

    override fun onClick(pMouseX: Double, pMouseY: Double) {

    }
}