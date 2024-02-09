/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.entity

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.util.cobblemonResource
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.FishingBobberEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.projectile.FishingBobberEntity
import net.minecraft.item.Items
import net.minecraft.util.Arm
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RotationAxis
import org.joml.Matrix3f
import org.joml.Matrix4f

/*
* Decompiled with CFR 0.2.0 (FabricMC d28b102d).
*/
@Environment(value = EnvType.CLIENT)
class PokeBobberEntityRenderer(context: EntityRendererFactory.Context?) : EntityRenderer<FishingBobberEntity>(context) {
    override fun render(fishingBobberEntity: FishingBobberEntity, f: Float, g: Float, matrixStack: MatrixStack, vertexConsumerProvider: VertexConsumerProvider, i: Int) {
        var s: Double
        val r: Float
        val q: Double
        val p: Double
        val o: Double
        val playerEntity = fishingBobberEntity.playerOwner ?: return
        matrixStack.push()
        matrixStack.push()
        matrixStack.scale(0.5f, 0.5f, 0.5f)
        matrixStack.multiply(dispatcher.rotation)
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f))
        val entry = matrixStack.peek()
        val matrix4f = entry.positionMatrix
        val matrix3f = entry.normalMatrix
        val vertexConsumer = vertexConsumerProvider.getBuffer(PokeBobberEntityRenderer.Companion.LAYER)
        PokeBobberEntityRenderer.Companion.vertex(vertexConsumer, matrix4f, matrix3f, i, 0.0f, 0, 0, 1)
        PokeBobberEntityRenderer.Companion.vertex(vertexConsumer, matrix4f, matrix3f, i, 1.0f, 0, 1, 1)
        PokeBobberEntityRenderer.Companion.vertex(vertexConsumer, matrix4f, matrix3f, i, 1.0f, 1, 1, 0)
        PokeBobberEntityRenderer.Companion.vertex(vertexConsumer, matrix4f, matrix3f, i, 0.0f, 1, 0, 0)
        matrixStack.pop()
        var j = if (playerEntity.mainArm == Arm.RIGHT) 1 else -1
        val itemStack = playerEntity.mainHandStack
        if (!itemStack.isOf(CobblemonItems.POKEROD)) {
            j = -j
        }
        val h = playerEntity.getHandSwingProgress(g)
        val k = MathHelper.sin(MathHelper.sqrt(h) * Math.PI.toFloat())
        val l = MathHelper.lerp(g, playerEntity.prevBodyYaw, playerEntity.bodyYaw) * (Math.PI.toFloat() / 180)
        val d = MathHelper.sin(l).toDouble()
        val e = MathHelper.cos(l).toDouble()
        val m = j.toDouble() * 0.35
        val n = 0.8
        if (dispatcher.gameOptions != null && !dispatcher.gameOptions.perspective.isFirstPerson || playerEntity !== MinecraftClient.getInstance().player) {
            o = MathHelper.lerp(g.toDouble(), playerEntity.prevX, playerEntity.x) - e * m - d * 0.8
            p = playerEntity.prevY + playerEntity.standingEyeHeight.toDouble() + (playerEntity.y - playerEntity.prevY) * g.toDouble() - 0.45
            q = MathHelper.lerp(g.toDouble(), playerEntity.prevZ, playerEntity.z) - d * m + e * 0.8
            r = if (playerEntity.isInSneakingPose) -0.1875f else 0.0f
        } else {
            s = 960.0 / dispatcher.gameOptions.fov.value.toDouble()
            var vec3d = dispatcher.camera.projection.getPosition(j.toFloat() * 0.525f, -0.1f)
            vec3d = vec3d.multiply(s)
            vec3d = vec3d.rotateY(k * 0.5f)
            vec3d = vec3d.rotateX(-k * 0.7f)
            o = MathHelper.lerp(g.toDouble(), playerEntity.prevX, playerEntity.getX()) + vec3d.x
            p = MathHelper.lerp(g.toDouble(), playerEntity.prevY, playerEntity.getY()) + vec3d.y
            q = MathHelper.lerp(g.toDouble(), playerEntity.prevZ, playerEntity.getZ()) + vec3d.z
            r = playerEntity.getStandingEyeHeight()
        }
        s = MathHelper.lerp(g.toDouble(), fishingBobberEntity.prevX, fishingBobberEntity.x)
        val t = MathHelper.lerp(g.toDouble(), fishingBobberEntity.prevY, fishingBobberEntity.y) + 0.25
        val u = MathHelper.lerp(g.toDouble(), fishingBobberEntity.prevZ, fishingBobberEntity.z)
        val v = (o - s).toFloat()
        val w = (p - t).toFloat() + r
        val x = (q - u).toFloat()
        val vertexConsumer2 = vertexConsumerProvider.getBuffer(RenderLayer.getLineStrip())
        val entry2 = matrixStack.peek()
        val y = 16
        for (z in 0..16) {
            renderFishingLine(v, w, x, vertexConsumer2, entry2, percentage(z, 16), percentage(z + 1, 16))
        }
        matrixStack.pop()
        super.render(fishingBobberEntity, f, g, matrixStack, vertexConsumerProvider, i)
    }

    override fun getTexture(fishingBobberEntity: FishingBobberEntity): Identifier {
        return TEXTURE
    }

    companion object {
        private val TEXTURE = cobblemonResource("textures/item/fishing/pokeball_bobber.png")
        private val LAYER = RenderLayer.getEntityCutout(TEXTURE)
        private const val field_33632 = 960.0

        @JvmStatic
        private fun percentage(value: Int, max: Int): Float {
            return value.toFloat() / max.toFloat()
        }

        private fun vertex(buffer: VertexConsumer, matrix: Matrix4f, normalMatrix: Matrix3f, light: Int, x: Float, y: Int, u: Int, v: Int) {
            buffer.vertex(matrix, x - 0.5f, y.toFloat() - 0.5f, 0.0f).color(255, 255, 255, 255).texture(u.toFloat(), v.toFloat()).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0f, 1.0f, 0.0f).next()
        }

        @JvmStatic
        private fun renderFishingLine(x: Float, y: Float, z: Float, buffer: VertexConsumer, matrices: MatrixStack.Entry, segmentStart: Float, segmentEnd: Float) {
            val f = x * segmentStart
            val g = y * (segmentStart * segmentStart + segmentStart) * 0.5f + 0.25f
            val h = z * segmentStart
            var i = x * segmentEnd - f
            var j = y * (segmentEnd * segmentEnd + segmentEnd) * 0.5f + 0.25f - g
            var k = z * segmentEnd - h
            val l = MathHelper.sqrt(i * i + j * j + k * k)
            i /= l
            j /= l
            k /= l
            buffer.vertex(matrices.positionMatrix, f, g, h).color(0, 0, 0, 255).normal(matrices.normalMatrix, i, j, k).next()
        }
    }
}
