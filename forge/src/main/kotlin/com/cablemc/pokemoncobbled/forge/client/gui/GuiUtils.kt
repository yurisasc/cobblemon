package com.cablemc.pokemoncobbled.forge.client.gui

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferUploader
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.math.Matrix4f
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.TextComponent
import net.minecraft.resources.ResourceLocation

fun blitk(
    poseStack: PoseStack,
    texture: ResourceLocation? = null,
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
    alpha: Number = 1F
) {
    RenderSystem.setShader { GameRenderer.getPositionTexShader() }
    texture?.run { RenderSystem.setShaderTexture(0, this) }
    RenderSystem.setShaderColor(red.toFloat(), green.toFloat(), blue.toFloat(), alpha.toFloat())
    RenderSystem.enableBlend()
    RenderSystem.defaultBlendFunc()
    RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)

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
    bufferbuilder.vertex(matrix, x, endY, blitOffset).uv(minU, maxV).endVertex()
    bufferbuilder.vertex(matrix, endX, endY, blitOffset).uv(maxU, maxV).endVertex()
    bufferbuilder.vertex(matrix, endX, y, blitOffset).uv(maxU, minV).endVertex()
    bufferbuilder.vertex(matrix, x, y, blitOffset).uv(minU, minV).endVertex()
    bufferbuilder.end()
    BufferUploader.end(bufferbuilder)
}

fun drawCenteredText(
    poseStack: PoseStack,
    font: ResourceLocation,
    text: Component,
    x: Number,
    y: Number,
    colour: Int,
    shadow: Boolean = true
) {
    val comp = (text as MutableComponent).withStyle(text.style.withFont(font))
    val mcFont = Minecraft.getInstance().font
    if (shadow)
        mcFont.drawShadow(poseStack, comp, x.toFloat() - mcFont.width(comp) / 2, y.toFloat(), colour)
    else
        mcFont.draw(poseStack, comp, x.toFloat() - mcFont.width(comp) / 2, y.toFloat(), colour)
}

fun drawText(
    poseStack: PoseStack,
    font: ResourceLocation,
    text: Component,
    x: Number,
    y: Number,
    colour: Int,
    shadow: Boolean = true
) {
    val comp = (text as MutableComponent).withStyle(text.style.withFont(font))
    val mcFont = Minecraft.getInstance().font
    if (shadow)
        mcFont.drawShadow(poseStack, comp, x.toFloat(), y.toFloat(), colour)
    else
        mcFont.draw(poseStack, comp, x.toFloat(), y.toFloat(), colour)
}

fun drawString(
    poseStack: PoseStack,
    text: String,
    x: Number,
    y: Number,
    colour: Int,
    shadow: Boolean = true,
    font: ResourceLocation? = null
) {
    val comp = TextComponent(text).also {
        font?.run {
            it.withStyle(it.style.withFont(this))
        }
    }
    val mcFont = Minecraft.getInstance().font
    if (shadow)
        mcFont.drawShadow(poseStack, comp, x.toFloat(), y.toFloat(), colour)
    else
        mcFont.draw(poseStack, comp, x.toFloat(), y.toFloat(), colour)
}