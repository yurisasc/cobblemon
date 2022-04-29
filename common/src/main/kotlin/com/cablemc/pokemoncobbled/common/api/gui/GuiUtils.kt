package com.cablemc.pokemoncobbled.common.api.gui

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferUploader
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.MatrixStack
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.math.Matrix4f
import net.minecraft.client.Minecraft
import net.minecraft.client.render.GameRenderer
import net.minecraft.text.Text
import net.minecraft.network.chat.MutableText
import net.minecraft.network.chat.LiteralText
import net.minecraft.util.Identifier

fun blitk(
    poseStack: MatrixStack,
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
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)
    }

    drawRectangle(
        poseStack.last().pose(),
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
    val bufferbuilder = Tesselator.getInstance().builder
    bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX)
    bufferbuilder.vertex(matrix, x, endY, blitOffset).uv(minU, maxV).next()
    bufferbuilder.vertex(matrix, endX, endY, blitOffset).uv(maxU, maxV).next()
    bufferbuilder.vertex(matrix, endX, y, blitOffset).uv(maxU, minV).next()
    bufferbuilder.vertex(matrix, x, y, blitOffset).uv(minU, minV).next()
    bufferbuilder.end()
    BufferUploader.end(bufferbuilder)
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
    val comp = (text as MutableText).withStyle(text.style.withFont(font))
    val mcFont = MinecraftClient.getInstance().font
    if (shadow)
        mcFont.drawShadow(poseStack, comp, x.toFloat() - mcFont.width(comp) / 2, y.toFloat(), colour)
    else
        mcFont.draw(poseStack, comp, x.toFloat() - mcFont.width(comp) / 2, y.toFloat(), colour)
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
    val comp = if (font == null) text else text.withStyle(text.style.withFont(font))
    val mcFont = MinecraftClient.getInstance().font
    var x = x
    if (centered) {
        val width = mcFont.width(comp)
        x = x.toDouble() - width / 2
    }

    if (shadow)
        mcFont.drawShadow(poseStack, comp, x.toFloat(), y.toFloat(), colour)
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
            it.withStyle(it.style.withFont(this))
        }
    }
    val mcFont = MinecraftClient.getInstance().font
    if (shadow)
        mcFont.drawShadow(poseStack, comp, x.toFloat(), y.toFloat(), colour)
    else
        mcFont.draw(poseStack, comp, x.toFloat(), y.toFloat(), colour)
}