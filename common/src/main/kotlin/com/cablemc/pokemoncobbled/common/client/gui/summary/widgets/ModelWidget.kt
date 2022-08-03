package com.cablemc.pokemoncobbled.common.client.gui.summary.widgets

import com.cablemc.pokemoncobbled.common.client.gui.drawProfilePokemon
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.PokemonFloatingState
import com.cablemc.pokemoncobbled.common.pokemon.RenderablePokemon
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText
import net.minecraft.util.math.Quaternion
import net.minecraft.util.math.Vec3f

class ModelWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    var pokemon: RenderablePokemon,
    val baseScale: Float = 2.7F
): SoundlessWidget(pX, pY, pWidth, pHeight, LiteralText("Summary - ModelWidget")) {

    companion object {
        var render = true
    }

    var state = PokemonFloatingState()
    private val minecraft = MinecraftClient.getInstance()
    private var rotVec = Vec3f(13F, 35F, 0F)

    override fun render(pMatrixStack: MatrixStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        if (!render) {
            return
        }
        hovered = pMouseX >= x && pMouseY >= y && pMouseX < x + width && pMouseY < y + height
        renderPKM(pMatrixStack)
    }

    private fun renderPKM(poseStack: MatrixStack) {
        poseStack.push()

        RenderSystem.enableScissor(
            (x * minecraft.window.scaleFactor).toInt(),
            (minecraft.window.height - (y * minecraft.window.scaleFactor) - (height * minecraft.window.scaleFactor)).toInt(),
            (width * minecraft.window.scaleFactor).toInt(),
            (height * minecraft.window.scaleFactor).toInt()
        )

        poseStack.translate(x + width * 0.5, y.toDouble(), 0.0)
        poseStack.scale(baseScale, baseScale, baseScale)
        poseStack.push()

        drawProfilePokemon(
            renderablePokemon = pokemon,
            matrixStack = poseStack,
            rotation = Quaternion.fromEulerXyzDegrees(rotVec),
            state = state
        )

        poseStack.pop()
        RenderSystem.disableScissor()

        poseStack.pop()
    }

    override fun onClick(pMouseX: Double, pMouseY: Double) {

    }
}