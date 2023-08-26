package com.cobblemon.mod.common.client.render.block

import com.cobblemon.mod.common.block.entity.FossilTubeBlockEntity
import com.cobblemon.mod.common.client.CobblemonBakingOverrides
import com.cobblemon.mod.common.client.render.renderQuad
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Direction
import org.joml.Vector3f

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
        if (entity.isOn) {
            val buffer = vertexConsumers?.getBuffer(RenderLayer.getTranslucent())
            LIQUID_MODEL.getQuads(entity.cachedState, null, entity.world?.random).forEach { quad ->
                buffer?.quad(matrices.peek(), quad, 0.75f, 0.75f, 0.75f, light, OverlayTexture.DEFAULT_UV)
            }

            /*
            MODEL_RENDERER.render(
                entity.world,
                LIQUID_MODEL,
                entity.cachedState,
                entity.pos,
                matrices,
                vertexConsumers?.getBuffer(RenderLayer.getCutout()),
                false,
                entity.world?.random,
                entity.cachedState?.getRenderingSeed(entity.pos)!!,
                OverlayTexture.DEFAULT_UV
            )
            */
        }
        matrices.pop()
    }

    companion object {
        val LIQUID_MODEL = CobblemonBakingOverrides.FOSSIL_TUB_LIQUID.getModel()
        val MODEL_RENDERER = MinecraftClient.getInstance().blockRenderManager.modelRenderer
    }
}