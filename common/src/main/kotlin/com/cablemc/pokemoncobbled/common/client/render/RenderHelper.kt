package com.cablemc.pokemoncobbled.common.client.render

import com.cablemc.pokemoncobbled.common.client.CobbledResources
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.Matrix3f
import net.minecraft.util.math.Matrix4f
import net.minecraft.util.math.Vec3f

fun renderImage(texture: Identifier, x: Double, y: Double, height: Double, width: Double) {
    val textureManager = MinecraftClient.getInstance().textureManager

    val buffer = Tessellator.getInstance().buffer
    buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE)
    textureManager.bindTexture(texture)

    buffer.vertex(x, y + height, 0.0).texture(0f, 1f).next()
    buffer.vertex(x + width, y + height, 0.0).texture(1f, 1f).next()
    buffer.vertex(x + width, y, 0.0).texture(1f, 0f).next()
    buffer.vertex(x, y, 0.0).texture(0f, 0f).next()

    Tessellator.getInstance().draw()
}

fun getDepletableRedGreen(
    ratio: Float,
    yellowRatio: Float = 0.5F,
    redRatio: Float = 0.2F
): Pair<Float, Float> {
    val m = -2

    val r = if (ratio > redRatio) {
        m * ratio - m
    } else {
        1.0
    }

    val g = if (ratio > yellowRatio) {
        1.0
    } else if (ratio > redRatio) {
        ratio * 1 / yellowRatio
    } else {
        0.0
    }

    return r.toFloat() to g.toFloat()
}

fun TextRenderer.drawScaled(
    matrixStack: MatrixStack,
    text: Text,
    x: Float,
    y: Float,
    scaleX: Float = 1F,
    scaleY: Float = 1F,
    colour: Int = 0xFFFFFF
) {
    matrixStack.push()
    matrixStack.scale(scaleX, scaleY, 1F)
    draw(matrixStack, text, x / scaleX, y / scaleY, colour)
    matrixStack.pop()
}


fun renderBeaconBeam(
    matrixStack: MatrixStack,
    buffer: VertexConsumerProvider,
    textureLocation: Identifier = CobbledResources.PHASE_BEAM,
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
    matrixStack.push()
    matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(beamRotation * 2.25f - 45.0f))
    var f9 = -beamRadius
    val f12 = -beamRadius
    renderPart(
        matrixStack,
        buffer.getBuffer(RenderLayer.getBeaconBeam(textureLocation, false)),
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
    matrixStack.pop()
    val f6 = -glowRadius
    val f7 = -glowRadius
    val f8 = -glowRadius
    f9 = -glowRadius
    renderPart(
        matrixStack,
        buffer.getBuffer(RenderLayer.getBeaconBeam(textureLocation, true)),
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
    matrixStack: MatrixStack,
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
    val pose = matrixStack.peek()
    val matrix4f = pose.positionMatrix
    val matrix3f = pose.normalMatrix
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
        .texture(texU, texV)
        .overlay(OverlayTexture.DEFAULT_UV)
        .light(15728880)
        .normal(matrixNormal, 0.0f, 1.0f, 0.0f)
        .next()
}