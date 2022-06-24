package com.cablemc.pokemoncobbled.common.api.gui

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.Matrix4f

fun blitk(
    matrixStack: MatrixStack,
    texture: Identifier? = null,
    x: Number,
    y: Number,
    height: Number = 0,
    width: Number = 0,
    uOffset: Number = 0,
    vOffset: Number = 0,
    textureWidth: Number = width,
    textureHeight: Number = height,
    blitOffset: Number = 0,
    red: Number = 1,
    green: Number = 1,
    blue: Number = 1,
    alpha: Number = 1F,
    blend: Boolean = true
) {
    RenderSystem.setShader { GameRenderer.getPositionTexShader() }
    texture?.run { RenderSystem.setShaderTexture(0, this) }
    RenderSystem.setShaderColor(red.toFloat(), green.toFloat(), blue.toFloat(), alpha.toFloat())
    if (blend) {
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA)
    }

    drawRectangle(
        matrixStack.peek().positionMatrix,
        x.toFloat(), y.toFloat(), x.toFloat() + width.toFloat(), y.toFloat() + height.toFloat(),
        blitOffset.toFloat(),
        uOffset.toFloat() / textureWidth.toFloat(), (uOffset.toFloat() + width.toFloat()) / textureWidth.toFloat(),
        vOffset.toFloat() / textureHeight.toFloat(), (vOffset.toFloat() + height.toFloat()) / textureHeight.toFloat()
    )
}

fun drawRectangle(
    matrix: Matrix4f,
    x: Float,
    y: Float,
    endX: Float,
    endY: Float,
    blitOffset: Float,
    minU: Float,
    maxU: Float,
    minV: Float,
    maxV: Float
) {
    val bufferbuilder = Tessellator.getInstance().buffer
    bufferbuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE)
    bufferbuilder.vertex(matrix, x, endY, blitOffset).texture(minU, maxV).next()
    bufferbuilder.vertex(matrix, endX, endY, blitOffset).texture(maxU, maxV).next()
    bufferbuilder.vertex(matrix, endX, y, blitOffset).texture(maxU, minV).next()
    bufferbuilder.vertex(matrix, x, y, blitOffset).texture(minU, minV).next()
    bufferbuilder.end()
    BufferRenderer.draw(bufferbuilder)
}

fun drawCenteredText(
    poseStack: MatrixStack,
    font: Identifier,
    text: Text,
    x: Number,
    y: Number,
    colour: Int,
    shadow: Boolean = true
) {
    val comp = (text as MutableText).setStyle(text.style.withFont(font))
    val mcFont = MinecraftClient.getInstance().textRenderer
    if (shadow)
        mcFont.drawWithShadow(poseStack, comp, x.toFloat() - mcFont.getWidth(comp) / 2, y.toFloat(), colour)
    else
        mcFont.draw(poseStack, comp, x.toFloat() - mcFont.getWidth(comp) / 2, y.toFloat(), colour)
}

fun drawText(
    poseStack: MatrixStack,
    font: Identifier? = null,
    text: MutableText,
    x: Number,
    y: Number,
    centered: Boolean = false,
    colour: Int,
    shadow: Boolean = true
) {
    val comp = if (font == null) text else text.setStyle(text.style.withFont(font))
    val mcFont = MinecraftClient.getInstance().textRenderer
    var x = x
    if (centered) {
        val width = mcFont.getWidth(comp)
        x = x.toDouble() - width / 2
    }
    if (shadow)
        mcFont.drawWithShadow(poseStack, comp, x.toFloat(), y.toFloat(), colour)
    else
        mcFont.draw(poseStack, comp, x.toFloat(), y.toFloat(), colour)
}

fun drawString(
    poseStack: MatrixStack,
    text: String,
    x: Number,
    y: Number,
    colour: Int,
    shadow: Boolean = true,
    font: Identifier? = null
) {
    val comp = LiteralText(text).also {
        font?.run {
            it.getWithStyle(it.style.withFont(this))
        }
    }
    val mcFont = MinecraftClient.getInstance().textRenderer
    if (shadow)
        mcFont.drawWithShadow(poseStack, comp, x.toFloat(), y.toFloat(), colour)
    else
        mcFont.draw(poseStack, comp, x.toFloat(), y.toFloat(), colour)
}