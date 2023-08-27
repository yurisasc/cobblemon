package com.cobblemon.mod.common.client.render.block

import com.cobblemon.mod.common.block.entity.fossil.FossilTubeBlockEntity
import com.cobblemon.mod.common.client.CobblemonBakingOverrides
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Direction.Axis
import net.minecraft.util.math.RotationAxis

class FossilTubeRenderer(ctx: BlockEntityRendererFactory.Context) : BlockEntityRenderer<FossilTubeBlockEntity> {
    override fun render(
        entity: FossilTubeBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider?,
        light: Int,
        overlay: Int
    ) {
        val connectionDir = entity.connectorPosition
        //FYI, rendering models this way ignores the pivots set in the model, so set the pivots manually
        when (connectionDir) {
            Direction.NORTH -> {
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(0f), 0.5f, 0f, 0.5f)
            }
            Direction.EAST -> {
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270f), 0.5f, 0f, 0.5f)
            }
            Direction.SOUTH -> {
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f), 0.5f, 0f, 0.5f)
            }
            Direction.WEST -> {
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90f), 0.5f, 0f, 0.5f)
            }

            else -> {}
        }

        val cutoutBuffer = vertexConsumers?.getBuffer(RenderLayer.getCutout())
        if (connectionDir != null) {
            matrices.push()
            CONNECTOR_MODEL.getQuads(entity.cachedState, null, entity.world?.random).forEach { quad ->
                cutoutBuffer?.quad(matrices.peek(), quad, 0.75f, 0.75f, 0.75f, light, OverlayTexture.DEFAULT_UV)
            }
            matrices.pop()
        }
        val fillLevel = entity.fillLevel
        if (fillLevel == 0) {
            return
        }
        matrices.push()
        val transparentBuffer = vertexConsumers?.getBuffer(RenderLayer.getTranslucent())

        val fluidModel = FLUID_MODELS[fillLevel-1]
        fluidModel.getQuads(entity.cachedState, null, entity.world?.random).forEach { quad ->
            transparentBuffer?.quad(matrices.peek(), quad, 0.75f, 0.75f, 0.75f, light, OverlayTexture.DEFAULT_UV)
        }

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
            CobblemonBakingOverrides.FOSSIL_FLUID_BUBBLING.getModel()
        )

        val CONNECTOR_MODEL = CobblemonBakingOverrides.RESTORATION_TANK_CONNECTOR.getModel()
    }
}