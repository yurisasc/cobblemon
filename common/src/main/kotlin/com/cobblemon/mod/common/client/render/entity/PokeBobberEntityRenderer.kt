/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.entity

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.entity.fishing.PokeRodFishingBobberEntity
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
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Arm
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RotationAxis

@Environment(value = EnvType.CLIENT)
class PokeBobberEntityRenderer(context: EntityRendererFactory.Context?) : EntityRenderer<PokeRodFishingBobberEntity>(context) {

    private var lastSpinAngle: Float = 0f
    private var randomPitch: Float = 0f
    private var randomYaw: Float = 0f

    override fun render(fishingBobberEntity: PokeRodFishingBobberEntity, f: Float, g: Float, matrixStack: MatrixStack, vertexConsumerProvider: VertexConsumerProvider, light: Int) {
        var s: Double
        val r: Float
        val q: Double
        val p: Double
        val o: Double
        val playerEntity = fishingBobberEntity.playerOwner ?: return
        matrixStack.push()

        // Check if the bobber has just been cast
        if (fishingBobberEntity.age <= 1) { // Adjust this check as needed
            // Generate new random pitch and yaw for each cast
            randomPitch = (Math.random() * 360).toFloat()
            randomYaw = (Math.random() * 360).toFloat()
        }

        val ballStack = CobblemonItems.POKE_BALL.defaultStack

        matrixStack.push()

        // Apply spinning effect only if the bobber is in the air
        // Modify spinning effect based on whether the bobber is in open water
        if (!fishingBobberEntity.isOnGround) {
            // You might not need to change stopRotationAge if you want the slowing to happen faster
            val stopRotationAge = 220 // This could remain as your baseline for when rotation completely stops

            // Adjust ageFactor calculation for faster slowing in open water
            val ageFactor = if (fishingBobberEntity.age < stopRotationAge) {
                if (fishingBobberEntity.inOpenWater) {
                    1 + fishingBobberEntity.age / 25.0 // Slows down much faster in open water
                } else {
                    1 + fishingBobberEntity.age / 100.0 // Standard slowing rate
                }
            } else {
                Double.POSITIVE_INFINITY // Stops spinning
            }

            val adjustedAgeFactor = if (ageFactor > 0) ageFactor else 1.0

            if (fishingBobberEntity.age < stopRotationAge) {
                lastSpinAngle = (((fishingBobberEntity.age + g) * 20 / adjustedAgeFactor) % 360).toFloat()
            }
        }

        // Apply random pitch and yaw before rendering the Poke Ball
        matrixStack.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(randomPitch))
        matrixStack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(randomYaw))

        // Apply rotation based on last spin angle
        matrixStack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(lastSpinAngle))

        MinecraftClient.getInstance().itemRenderer.renderItem(
            ballStack,
            ModelTransformationMode.GROUND,
            light,
            OverlayTexture.DEFAULT_UV,
            matrixStack,
            vertexConsumerProvider,
            fishingBobberEntity.world,
            0
        )
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
        for (z in 0..16) {
            renderFishingLine(v, w, x, vertexConsumer2, entry2, percentage(z, 16), percentage(z + 1, 16))
        }
        matrixStack.pop()
        super.render(fishingBobberEntity, f, g, matrixStack, vertexConsumerProvider, light)
    }

    companion object {
        @JvmStatic
        private fun percentage(value: Int, max: Int): Float {
            return value.toFloat() / max.toFloat()
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

    override fun getTexture(entity: PokeRodFishingBobberEntity): Identifier {
        return cobblemonResource("textures/item/fishing/pokeball_bobber.png")
    }
}
