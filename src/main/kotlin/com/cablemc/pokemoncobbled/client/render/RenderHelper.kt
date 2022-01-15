package com.cablemc.pokemoncobbled.client.render

import com.cablemc.pokemoncobbled.client.CobbledResources
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.math.Matrix3f
import com.mojang.math.Matrix4f
import com.mojang.math.Vector3f
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture
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


fun renderBeaconBeam(
    matrixStack: PoseStack,
    buffer: MultiBufferSource,
    textureLocation: ResourceLocation = CobbledResources.PHASE_BEAM,
    partialTicks: Float,
    totalLevelTime: Long,
    yOffset: Float = 0F,
    height: Float,
    red: Float,
    green: Float,
    blue: Float,
    alpha: Float,
    beamRadius: Float,
    glowRadius: Float,
    glowAlpha: Float
) {
    val i = yOffset + height
    val beamRotation = Math.floorMod(totalLevelTime, 40).toFloat() + partialTicks
    matrixStack.pushPose()
    matrixStack.mulPose(Vector3f.YP.rotationDegrees(beamRotation * 2.25f - 45.0f))
    var f9 = -beamRadius
    val f12 = -beamRadius
    renderPart(
        matrixStack,
        buffer.getBuffer(RenderType.beaconBeam(textureLocation, false)),
        red,
        green,
        blue,
        alpha,
        yOffset,
        i,
        0.0f,
        beamRadius,
        beamRadius,
        0.0f,
        f9,
        0.0f,
        0.0f,
        f12
    )
    // Undo the rotation so that the glow is at a rotated offset
    matrixStack.popPose()
    val f6 = -glowRadius
    val f7 = -glowRadius
    val f8 = -glowRadius
    f9 = -glowRadius
    renderPart(
        matrixStack,
        buffer.getBuffer(RenderType.beaconBeam(textureLocation, true)),
        red,
        green,
        blue,
        glowAlpha,
        yOffset,
        i,
        f6,
        f7,
        glowRadius,
        f8,
        f9,
        glowRadius,
        glowRadius,
        glowRadius
    )
}

fun renderPart(
    matrixStack: PoseStack,
    vertexBuffer: VertexConsumer,
    red: Float,
    green: Float,
    blue: Float,
    alpha: Float,
    yMin: Float,
    yMax: Float,
    p_112164_: Float,
    p_112165_: Float,
    p_112166_: Float,
    p_112167_: Float,
    p_112168_: Float,
    p_112169_: Float,
    p_112170_: Float,
    p_112171_: Float
) {
    val pose = matrixStack.last()
    val matrix4f = pose.pose()
    val matrix3f = pose.normal()
    renderQuad(
        matrix4f,
        matrix3f,
        vertexBuffer,
        red,
        green,
        blue,
        alpha,
        yMin,
        yMax,
        p_112164_,
        p_112165_,
        p_112166_,
        p_112167_
    )
    renderQuad(
        matrix4f,
        matrix3f,
        vertexBuffer,
        red,
        green,
        blue,
        alpha,
        yMin,
        yMax,
        p_112170_,
        p_112171_,
        p_112168_,
        p_112169_
    )
    renderQuad(
        matrix4f,
        matrix3f,
        vertexBuffer,
        red,
        green,
        blue,
        alpha,
        yMin,
        yMax,
        p_112166_,
        p_112167_,
        p_112170_,
        p_112171_
    )
    renderQuad(
        matrix4f,
        matrix3f,
        vertexBuffer,
        red,
        green,
        blue,
        alpha,
        yMin,
        yMax,
        p_112168_,
        p_112169_,
        p_112164_,
        p_112165_
    )
}

fun renderQuad(
    matrixPos: Matrix4f,
    matrixNormal: Matrix3f,
    buffer: VertexConsumer,
    red: Float,
    green: Float,
    blue: Float,
    alpha: Float,
    yMin: Float,
    yMax: Float,
    x1: Float,
    z1: Float,
    x2: Float,
    z2: Float
) {
    addVertex(matrixPos, matrixNormal, buffer, red, green, blue, alpha, yMax, x1, z1, 1F, 0F)
    addVertex(matrixPos, matrixNormal, buffer, red, green, blue, alpha, yMin, x1, z1, 1F, 1F)
    addVertex(matrixPos, matrixNormal, buffer, red, green, blue, alpha, yMin, x2, z2, 0F, 1F)
    addVertex(matrixPos, matrixNormal, buffer, red, green, blue, alpha, yMax, x2, z2, 0F, 0F)
}

fun addVertex(
    matrixPos: Matrix4f,
    matrixNormal: Matrix3f,
    buffer: VertexConsumer,
    red: Float,
    green: Float,
    blue: Float,
    alpha: Float,
    y: Float,
    x: Float,
    z: Float,
    texU: Float,
    texV: Float
) {
    buffer
        .vertex(matrixPos, x, y, z)
        .color(red, green, blue, alpha)
        .uv(texU, texV)
        .overlayCoords(OverlayTexture.NO_OVERLAY)
        .uv2(15728880)
        .normal(matrixNormal, 0.0f, 1.0f, 0.0f)
        .endVertex()
}