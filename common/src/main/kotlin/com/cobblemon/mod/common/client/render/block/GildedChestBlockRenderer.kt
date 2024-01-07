package com.cobblemon.mod.common.client.render.block

import com.cobblemon.mod.common.CobblemonBlocks
import com.cobblemon.mod.common.block.entity.GildedChestBlockEntity
import com.cobblemon.mod.common.block.multiblock.FossilMultiblockStructure
import com.cobblemon.mod.common.client.render.models.blockbench.repository.BlockEntityModelRepository
import com.cobblemon.mod.common.client.render.models.blockbench.repository.FossilModelRepository
import com.cobblemon.mod.common.client.render.models.blockbench.repository.RenderContext
import com.cobblemon.mod.common.client.render.models.blockbench.setRotation
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.math.geometry.Axis
import com.cobblemon.mod.common.util.math.geometry.toRadians
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Direction
import net.minecraft.util.math.RotationAxis

class GildedChestBlockRenderer(context: BlockEntityRendererFactory.Context) : BlockEntityRenderer<GildedChestBlockEntity> {
    override fun render(
        entity: GildedChestBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        val aspects = emptySet<String>()
        val state = entity.poseableState
        state.updatePartialTicks(tickDelta)

        val model = BlockEntityModelRepository.getPoser(POSER_ID, aspects)
        val texture = BlockEntityModelRepository.getTexture(POSER_ID, aspects, state.animationSeconds)
        val vertexConsumer = vertexConsumers.getBuffer(model.getLayer(texture))
        model.bufferProvider = vertexConsumers
        state.currentModel = model

        matrices.push()
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180f))
        matrices.translate(-0.5, 0.0, 0.5);

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
        model.withLayerContext(vertexConsumers, state, BlockEntityModelRepository.getLayers(POSER_ID, aspects)) {
            model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1F, 1F, 1F, 1F)
        }
        model.setDefault()
        matrices.pop()

    }

    companion object {
        val POSER_ID = cobblemonResource("gilded_chest")
        val TEXTURE_ID = cobblemonResource("textures/block/gilded_chest.png")
    }
}