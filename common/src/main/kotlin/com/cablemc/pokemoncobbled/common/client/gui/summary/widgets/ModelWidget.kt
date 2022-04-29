package com.cablemc.pokemoncobbled.common.client.gui.summary.widgets

import com.cablemc.pokemoncobbled.common.client.gui.drawProfilePokemon
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.MatrixStack
import com.mojang.math.Quaternion
import com.mojang.math.Vec3f
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.LiteralText

class ModelWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    pokemon: Pokemon
): SoundlessWidget(pX, pY, pWidth, pHeight, LiteralText("Summary - ModelWidget")) {

    companion object {
        var render = true
    }

    var pokemon = pokemon
    private val minecraft = MinecraftClient.getInstance()
    private var rotVec = Vec3f(13F, 35F, 0F)

    override fun render(pMatrixStack: MatrixStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        if (!render) {
            return
        }
        isHovered = pMouseX >= x && pMouseY >= y && pMouseX < x + width && pMouseY < y + height
        renderPKM(pMatrixStack)
    }

    private fun renderPKM(poseStack: MatrixStack) {
        poseStack.push()

        RenderSystem.enableScissor(
            (x * minecraft.window.guiScale).toInt(),
            (minecraft.window.height - (y * minecraft.window.guiScale) - (height * minecraft.window.guiScale)).toInt(),
            (width * minecraft.window.guiScale).toInt(),
            (height * minecraft.window.guiScale).toInt()
        )

        val baseScale = 1.5F
        poseStack.translate(x + width * 0.5, y.toDouble() + height / 2 / baseScale, 0.0)
        poseStack.scale(baseScale, baseScale, baseScale)
        poseStack.push()

        drawProfilePokemon(
            pokemon = pokemon,
            poseStack = poseStack,
            rotation = Quaternion.fromXYZDegrees(rotVec)
        )

        poseStack.pop()
        RenderSystem.disableScissor()

        poseStack.pop()
    }

    override fun onClick(pMouseX: Double, pMouseY: Double) {

    }
}