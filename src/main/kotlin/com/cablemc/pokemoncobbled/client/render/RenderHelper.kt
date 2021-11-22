package com.cablemc.pokemoncobbled.client.render

import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation

fun renderImage(texture: ResourceLocation, x: Double, y: Double, height: Double, width: Double) {
    val textureManager = Minecraft.getInstance().textureManager

    val buffer = Tesselator.getInstance().builder
    buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX)
    textureManager.bindForSetup(texture)

    buffer.vertex(x, y + height, 0.0).uv(0f, 1f).endVertex()
    buffer.vertex(x + width, y + height, 0.0).uv(1f, 1f).endVertex()
    buffer.vertex(x + width, y, 0.0).uv(1f, 0f).endVertex()
    buffer.vertex(x, y, 0.0).uv(0f, 0f).endVertex()

    Tesselator.getInstance().end()
}