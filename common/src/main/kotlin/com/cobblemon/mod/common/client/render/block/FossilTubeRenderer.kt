package com.cobblemon.mod.common.client.render.block

import com.cobblemon.mod.common.block.entity.fossil.FossilTubeBlockEntity
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
        matrices.push()
        if (entity.fillLevel == 8) {
            val buffer = vertexConsumers?.getBuffer(RenderLayer.getTranslucent())
            LIQUID_MODEL.getQuads(entity.cachedState, null, entity.world?.random).forEach { quad ->
                buffer?.quad(matrices.peek(), quad, 0.75f, 0.75f, 0.75f, light, OverlayTexture.DEFAULT_UV)
            }
        }
        matrices.pop()
    }

    companion object {
        val LIQUID_MODEL = CobblemonBakingOverrides.FOSSIL_TUB_LIQUID.getModel()
    }
}