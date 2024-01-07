/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.block

import com.cobblemon.mod.common.CobblemonBlocks
import com.cobblemon.mod.common.block.entity.fossil.FossilTubeBlockEntity
import com.cobblemon.mod.common.block.multiblock.FossilMultiblockStructure
import com.cobblemon.mod.common.client.CobblemonBakingOverrides
import com.cobblemon.mod.common.client.render.models.blockbench.repository.FossilModelRepository
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Direction
import net.minecraft.util.math.RotationAxis

class FossilTubeRenderer(ctx: BlockEntityRendererFactory.Context) : BlockEntityRenderer<FossilTubeBlockEntity> {
    override fun render(
        entity: FossilTubeBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        if (entity.multiblockStructure == null) {
            return
        }
        val struct = entity.multiblockStructure as FossilMultiblockStructure
        val connectionDir = struct.tubeConnectorDirection
        // FYI, rendering models this way ignores the pivots set in the model, so set the pivots manually
        when (connectionDir) {
            Direction.NORTH -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(0f), 0.5f, 0f, 0.5f)
            Direction.EAST -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270f), 0.5f, 0f, 0.5f)
            Direction.SOUTH -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f), 0.5f, 0f, 0.5f)
            Direction.WEST -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90f), 0.5f, 0f, 0.5f)
            else -> {}
        }

        val cutoutBuffer = vertexConsumers.getBuffer(RenderLayer.getCutout())
        if (connectionDir != null) {
            matrices.push()
            CONNECTOR_MODEL.getQuads(entity.cachedState, null, entity.world?.random).forEach { quad ->
                cutoutBuffer.quad(matrices.peek(), quad, 0.75f, 0.75f, 0.75f, light, OverlayTexture.DEFAULT_UV)
            }
            matrices.pop()
        }
        val fillLevel = struct.fillLevel
        if (fillLevel == 0 && struct.createdPokemon == null) {
            return
        }

        if (struct.isRunning() or (struct.createdPokemon != null)) renderBaby(entity, tickDelta, matrices, vertexConsumers, light, overlay)

        matrices.push()
        val transparentBuffer = vertexConsumers.getBuffer(RenderLayer.getTranslucent())

        val fluidModel = if (struct.isRunning()) FLUID_MODELS[8]
        else if (struct.createdPokemon != null) FLUID_MODELS[7]
        else FLUID_MODELS[fillLevel-1]
        fluidModel.getQuads(entity.cachedState, null, entity.world?.random).forEach { quad ->
            transparentBuffer?.quad(matrices.peek(), quad, 0.75f, 0.75f, 0.75f, light, OverlayTexture.DEFAULT_UV)
        }


        matrices.pop()
    }

    private fun renderBaby(
        entity: FossilTubeBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        val struc = entity.multiblockStructure as? FossilMultiblockStructure ?: return
        val fossil = struc.resultingFossil ?: return
        val timeRemaining = struc.timeRemaining

        val tankBlockState = entity.world?.getBlockState(entity.pos) ?: return
        if(tankBlockState.block != CobblemonBlocks.FOSSIL_TUBE) {
            // Block has been destroyed/replaced
            return
        }
        val tankDirection = tankBlockState.get(HorizontalFacingBlock.FACING)
        val struct = entity.multiblockStructure as FossilMultiblockStructure
        val connectionDir = struct.tubeConnectorDirection

        val aspects = emptySet<String>()
        val state = struc.fossilState
        state.updatePartialTicks(tickDelta)

        val model = FossilModelRepository.getPoser(fossil.identifier, aspects)
        val texture = FossilModelRepository.getTexture(fossil.identifier, aspects, state.animationSeconds)
        val vertexConsumer = vertexConsumers.getBuffer(model.getLayer(texture))
        val pose = model.poses.values.first()
        model.bufferProvider = vertexConsumers
        state.currentModel = model
        state.setPose(pose.poseName)
        state.timeEnteredPose = 0F

        val scale: Float = if (timeRemaining == 0) {
            model.maxScale
        } else {
            (1 - (timeRemaining / FossilMultiblockStructure.TIME_TO_TAKE.toFloat())) * model.maxScale
        }

        matrices.push()
        matrices.translate(0.5, 0.5 + model.yTranslation, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180F))
        if(tankDirection.rotateCounterclockwise(Direction.Axis.Y) == connectionDir) {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90F))
        } else if(tankDirection == connectionDir) {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180F))
        } else if(tankDirection.opposite != connectionDir) {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90F))
        }

        matrices.push()
        matrices.scale(scale, scale, scale)

        model.setupAnimStateful(
            entity = null,
            state = state,
            headYaw = 0F,
            headPitch = 0F,
            limbSwing = 0F,
            limbSwingAmount = 0F,
            ageInTicks = state.animationSeconds * 20
        )
        model.render(matrices, vertexConsumer, light, overlay, 1.0f, 1.0f, 1.0f, 1.0f)
        model.withLayerContext(vertexConsumers, state, FossilModelRepository.getLayers(fossil.identifier, aspects)) {
            model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1F, 1F, 1F, 1F)
        }
        model.setDefault()
        matrices.pop()
        matrices.pop()

    }

    companion object {
        val FLUID_MODELS = listOf(
            CobblemonBakingOverrides.FOSSIL_FLUID_CHUNKED_1.getModel(),
            CobblemonBakingOverrides.FOSSIL_FLUID_CHUNKED_2.getModel(),
            CobblemonBakingOverrides.FOSSIL_FLUID_CHUNKED_3.getModel(),
            CobblemonBakingOverrides.FOSSIL_FLUID_CHUNKED_4.getModel(),
            CobblemonBakingOverrides.FOSSIL_FLUID_CHUNKED_5.getModel(),
            CobblemonBakingOverrides.FOSSIL_FLUID_CHUNKED_6.getModel(),
            CobblemonBakingOverrides.FOSSIL_FLUID_CHUNKED_7.getModel(),
            CobblemonBakingOverrides.FOSSIL_FLUID_CHUNKED_8.getModel(),
            CobblemonBakingOverrides.FOSSIL_FLUID_BUBBLING.getModel()
        )

        val CONNECTOR_MODEL = CobblemonBakingOverrides.RESTORATION_TANK_CONNECTOR.getModel()
    }
}