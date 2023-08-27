package com.cobblemon.mod.common.client.render.block

import com.cobblemon.mod.common.block.entity.fossil.FossilTubeBlockEntity
import com.cobblemon.mod.common.block.fossilmachine.FossilTubeBlock
import com.cobblemon.mod.common.client.CobblemonBakingOverrides
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack

class FossilTubeRenderer(ctx: BlockEntityRendererFactory.Context) : BlockEntityRenderer<FossilTubeBlockEntity> {
    override fun render(
        entity: FossilTubeBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider?,
        light: Int,
        overlay: Int
    ) {
        val fillLevel = entity.fillLevel
        if (fillLevel == 0) {
            return
        }
        matrices.push()
        val buffer = vertexConsumers?.getBuffer(RenderLayer.getTranslucent())

        val model = CHUNKED_MODELS[fillLevel-1]
        model.getQuads(entity.cachedState, null, entity.world?.random).forEach { quad ->
            buffer?.quad(matrices.peek(), quad, 0.75f, 0.75f, 0.75f, light, OverlayTexture.DEFAULT_UV)
        }
        matrices.pop()
    }

    companion object {
        val CHUNKED_MODELS = listOf(
            CobblemonBakingOverrides.FOSSIL_FLUID_CHUNKED_1.getModel(),
            CobblemonBakingOverrides.FOSSIL_FLUID_CHUNKED_2.getModel(),
            CobblemonBakingOverrides.FOSSIL_FLUID_CHUNKED_3.getModel(),
            CobblemonBakingOverrides.FOSSIL_FLUID_CHUNKED_4.getModel(),
            CobblemonBakingOverrides.FOSSIL_FLUID_CHUNKED_5.getModel(),
            CobblemonBakingOverrides.FOSSIL_FLUID_CHUNKED_6.getModel(),
            CobblemonBakingOverrides.FOSSIL_FLUID_CHUNKED_7.getModel(),
            CobblemonBakingOverrides.FOSSIL_FLUID_BUBBLING.getModel()
        )
    }
}