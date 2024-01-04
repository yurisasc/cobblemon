package com.cobblemon.mod.common.client.render.block

import com.cobblemon.mod.common.block.entity.GildedChestBlockEntity
import com.cobblemon.mod.common.client.render.models.blockbench.repository.BlockEntityModelRepository
import com.cobblemon.mod.common.client.render.models.blockbench.repository.RenderContext
import com.cobblemon.mod.common.client.render.models.blockbench.setRotation
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.math.geometry.Axis
import com.cobblemon.mod.common.util.math.geometry.toRadians
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
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
        val state = entity.poseableState
        state.updatePartialTicks(tickDelta)
        val renderLayer = RenderLayer.getEntityCutout(TEXTURE_ID)
        val model = BlockEntityModelRepository.getPoser(POSER_ID, emptySet())
        //state.setPose(pose.poseName)
        matrices.push()

        if (state.currentPose == null) {
            state.setPose(model.poses.values.first().poseName)
            state.timeEnteredPose = 0F
        }
        if (state.currentPose == model.poses.values.first().poseName) {
            state.timeEnteredPose = 0F
            state.setPose(model.poses.values.last().poseName)
        }

        //Why is this necessary? The world may never know (prob model issues)
        matrices.translate(0.5F, 0F, 0.5F)
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180f))
        model.setupAnimStateful(
            entity = null,
            state = state,
            headYaw = 0F,
            headPitch = 0F,
            limbSwing = 0F,
            limbSwingAmount = 0F,
            ageInTicks = state.animationSeconds * 20
        )
        model.render(matrices, vertexConsumers.getBuffer(renderLayer), light, overlay, 1.0f, 1.0f, 1.0f, 1.0f)
        //model.render(matrices, vertexConsumers.getBuffer(renderLayer), LightmapTextureManager.MAX_LIGHT_COORDINATE, overlay)
        matrices.pop()
    }

    companion object {
        val POSER_ID = cobblemonResource("gilded_chest")
        val TEXTURE_ID = cobblemonResource("textures/block/gilded_chest.png")
    }
}