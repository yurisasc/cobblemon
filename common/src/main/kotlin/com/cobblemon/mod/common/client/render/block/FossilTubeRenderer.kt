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
        val state = entity.cachedState
        matrices.push()
        if (entity.fillLevel == 8) {
            val buffer = vertexConsumers?.getBuffer(RenderLayer.getTranslucent())
            BUBBLING_MODEL.getQuads(entity.cachedState, null, entity.world?.random).forEach { quad ->
                buffer?.quad(matrices.peek(), quad, 0.75f, 0.75f, 0.75f, light, OverlayTexture.DEFAULT_UV)
            }
        }
        else if (state.get(FossilTubeBlock.PART) == FossilTubeBlock.TubePart.BOTTOM){
            val buffer = vertexConsumers?.getBuffer(RenderLayer.getTranslucent())
            CHUNKED_MODEL.getQuads(entity.cachedState, null, entity.world?.random).forEach { quad ->
                buffer?.quad(matrices.peek(), quad, 0.75f, 0.75f, 0.75f, light, OverlayTexture.DEFAULT_UV)
            }
        }
        matrices.pop()
    }

    companion object {
        val BUBBLING_MODEL = CobblemonBakingOverrides.FOSSIL_FLUID_BUBBLING.getModel()
        val CHUNKED_MODEL = CobblemonBakingOverrides.FOSSIL_FLUID_CHUNKED.getModel()
    }
}