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

    override fun render(fishingBobberEntity: PokeRodFishingBobberEntity, elapsedPartialTicks: Float, tickDelta: Float, matrixStack: MatrixStack, vertexConsumerProvider: VertexConsumerProvider, light: Int) {
        var playerPosXWorld: Double
        val eyeHeightOffset: Float
        val playerPosZWorld: Double
        val playerPosYWorld: Double
        val playerEyeYWorld: Double
        val playerEntity = fishingBobberEntity.playerOwner ?: return
        matrixStack.push()

        // Generate controlled random pitch and yaw for each cast, with constraints
        if (fishingBobberEntity.age <= 1) {
            // Random yaw within a constrained range to allow for limited tilting
            randomYaw = (-180 + Math.random() * 360).toFloat() // Example: -180 to +180 degrees
            // Random pitch to ensure the top of the Poke Ball leans towards the string
            randomPitch = (-40 + Math.random() * 80).toFloat() // Example: -40 to +40 degrees
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
                    1 + fishingBobberEntity.age / 15.0 // Slows down much faster in open water
                } else {
                    1 + fishingBobberEntity.age / 100.0 // Standard slowing rate
                }
            } else {
                Double.POSITIVE_INFINITY // Stops spinning
            }

            // Ensure ageFactor does not reduce the rotation speed to 0 or negative
            val adjustedAgeFactor = if (ageFactor > 0) ageFactor else 1.0

            // Update and apply the spinning effect, incorporating the slowing factor
            // Stop increasing lastSpinAngle once the bobber reaches the stopRotationAge
            if (fishingBobberEntity.age < stopRotationAge) {
                lastSpinAngle = (((fishingBobberEntity.age + tickDelta) * 20 / adjustedAgeFactor) % 360).toFloat()
            }
            // After reaching stopRotationAge, lastSpinAngle remains constant, effectively stopping the rotation
        }

        // When in water, gradually make the Poke Ball upright
        if (fishingBobberEntity.state == PokeRodFishingBobberEntity.State.BOBBING && !fishingBobberEntity.isOnGround) {
            // Assuming randomPitch is the rotation around X axis that needs to be adjusted
            // Simple linear interpolation towards 0 degrees; adjust 'lerpFactor' as needed for speed
            val lerpFactor = 0.04f // This controls the speed of the adjustment
            randomPitch = MathHelper.lerp(lerpFactor, randomPitch, 0f) // Adjust towards 0 degrees within range
            // Consider similar logic for yaw if needed to maintain visual consistency
        }

        // Apply random pitch and yaw before rendering the Poke Ball
        matrixStack.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(randomPitch))
        matrixStack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(randomYaw))

        // Apply rotation based on last spin angle
        matrixStack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(lastSpinAngle))

        // Scale down the Poke Ball to 70% of its original size
        matrixStack.scale(0.7f, 0.7f, 0.7f) // Apply the scaling transformation

        // Translate the Poke Ball model visually (not the ideal approach we want. todo we want to lower the fishing line end to connect better
        //matrixStack.translate(0.0, 0.10, 0.0); // Raise the model by 0.15 on the y-axis

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
        var armOffset = if (playerEntity.mainArm == Arm.RIGHT) 1 else -1
        val itemStack = playerEntity.mainHandStack
        if (!itemStack.isOf(CobblemonItems.POKEROD)) {
            armOffset = -armOffset
        }
        val handSwingProgress = playerEntity.getHandSwingProgress(tickDelta)
        val swingAngle = MathHelper.sin(MathHelper.sqrt(handSwingProgress) * Math.PI.toFloat())
        val bodyYawRadians = MathHelper.lerp(tickDelta, playerEntity.prevBodyYaw, playerEntity.bodyYaw) * (Math.PI.toFloat() / 180)
        val sinBodyYaw = MathHelper.sin(bodyYawRadians).toDouble()
        val cosBodyYaw = MathHelper.cos(bodyYawRadians).toDouble()
        val horizontalOffset = armOffset.toDouble() * 0.35
        if (dispatcher.gameOptions != null && !dispatcher.gameOptions.perspective.isFirstPerson || playerEntity !== MinecraftClient.getInstance().player) {
            playerEyeYWorld = MathHelper.lerp(tickDelta.toDouble(), playerEntity.prevX, playerEntity.x) - cosBodyYaw * horizontalOffset - sinBodyYaw * 0.8
            playerPosYWorld = playerEntity.prevY + playerEntity.standingEyeHeight.toDouble() + (playerEntity.y - playerEntity.prevY) * tickDelta.toDouble() - 0.45
            playerPosZWorld = MathHelper.lerp(tickDelta.toDouble(), playerEntity.prevZ, playerEntity.z) - sinBodyYaw * horizontalOffset + cosBodyYaw * 0.8
            eyeHeightOffset = if (playerEntity.isInSneakingPose) -0.1875f else 0.0f
        } else {
            playerPosXWorld = 960.0 / dispatcher.gameOptions.fov.value.toDouble()
            var vec3d = dispatcher.camera.projection.getPosition(armOffset.toFloat() * 0.525f, -0.1f)
            vec3d = vec3d.multiply(playerPosXWorld)
            vec3d = vec3d.rotateY(swingAngle * 0.5f)
            vec3d = vec3d.rotateX(-swingAngle * 0.7f)
            playerEyeYWorld = MathHelper.lerp(tickDelta.toDouble(), playerEntity.prevX, playerEntity.getX()) + vec3d.x
            playerPosYWorld = MathHelper.lerp(tickDelta.toDouble(), playerEntity.prevY, playerEntity.getY()) + vec3d.y
            playerPosZWorld = MathHelper.lerp(tickDelta.toDouble(), playerEntity.prevZ, playerEntity.getZ()) + vec3d.z
            eyeHeightOffset = playerEntity.getStandingEyeHeight()
        }
        playerPosXWorld = MathHelper.lerp(tickDelta.toDouble(), fishingBobberEntity.prevX, fishingBobberEntity.x)

        val bobberPosY = MathHelper.lerp(tickDelta.toDouble(), fishingBobberEntity.prevY, fishingBobberEntity.y) + 0.25
        val bobberPosZ = MathHelper.lerp(tickDelta.toDouble(), fishingBobberEntity.prevZ, fishingBobberEntity.z)

        val deltaX = (playerEyeYWorld - playerPosXWorld).toFloat()
        val deltaY = (playerPosYWorld - bobberPosY).toFloat() + eyeHeightOffset
        val deltaZ = (playerPosZWorld - bobberPosZ).toFloat()
        val vertexConsumer2 = vertexConsumerProvider.getBuffer(RenderLayer.getLineStrip())
        val entry2 = matrixStack.peek()
        for (lineIndex in 0..16) {
            renderFishingLine(deltaX, deltaY, deltaZ, vertexConsumer2, entry2, percentage(lineIndex, 16), percentage(lineIndex + 1, 16))
        }
        matrixStack.pop()
        super.render(fishingBobberEntity, elapsedPartialTicks, tickDelta, matrixStack, vertexConsumerProvider, light)
    }

    companion object {
        @JvmStatic
        private fun percentage(value: Int, max: Int): Float {
            return value.toFloat() / max.toFloat()
        }

        @JvmStatic
        private fun renderFishingLine(deltaX: Float, deltaY: Float, deltaZ: Float, vertexBuffer: VertexConsumer, matrixEntry: MatrixStack.Entry, segmentStartFraction: Float, segmentEndFraction: Float) {

            // Calculate the starting X position of the current segment based on the start fraction
            val startX = deltaX * segmentStartFraction
            // Calculate the starting Y position of the current segment, adding a curvature effect and a base offset
            var startY = deltaY * (segmentStartFraction * segmentStartFraction + segmentStartFraction) * 0.5f + 0.25f
            // Calculate the starting Z position of the current segment based on the start fraction
            val startZ = deltaZ * segmentStartFraction

            // Calculate the change in X position from the start to the end of the current segment
            var deltaXSegment = deltaX * segmentEndFraction - startX
            // Calculate the change in Y position from the start to the end of the current segment, adjusting for curvature and base offset
            var deltaYSegment = deltaY * (segmentEndFraction * segmentEndFraction + segmentEndFraction) * 0.5f + 0.25f - startY
            // Calculate the change in Z position from the start to the end of the current segment
            var deltaZSegment = deltaZ * segmentEndFraction - startZ

            // Normalize the segment vector to use it for the normal vector in the vertex data
            val length = MathHelper.sqrt(deltaXSegment * deltaXSegment + deltaYSegment * deltaYSegment + deltaZSegment * deltaZSegment)
            deltaXSegment /= length
            deltaYSegment /= length
            deltaZSegment /= length

            // New factor to adjust the curve so it ends lower
            // This could be a simple linear adjustment or more complex based on your needs
            val loweringFactor = 1.0f - segmentEndFraction // Becomes 0 at the start and approaches 1 towards the end
            val additionalLowering = loweringFactor * 0.1f // Adjust this factor to control how much lower the end goes

            // Apply the lowering adjustment
            startY -= additionalLowering


            // todo find a better way to make the fishing line connect to the bobber
            // Adjust startY and deltaYSegment for the last segment to lower the end point closer to the Poke Ball
            /*if (segmentEndFraction == 0.0625f) {
                startY -= 0.1f // Lower the starting point of the last segment
                deltaYSegment -= 0.1f // Ensure the end point is also adjusted accordingly
            }*/

            // Add the vertex for the start of this segment
            vertexBuffer.vertex(matrixEntry.positionMatrix, startX, startY, startZ).color(0, 0, 0, 255).normal(matrixEntry.normalMatrix, deltaXSegment, deltaYSegment, deltaZSegment).next()
        }
    }

    override fun getTexture(entity: PokeRodFishingBobberEntity): Identifier {
        return cobblemonResource("textures/item/fishing/pokeball_bobber.png")
    }
}
