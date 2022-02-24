package com.cablemc.pokemoncobbled.common.client.render.pokeball

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.repository.PokeBallModelRepository
import com.cablemc.pokemoncobbled.common.entity.pokeball.EmptyPokeBallEntity
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation

class PokeBallRenderer(context: EntityRendererProvider.Context) : EntityRenderer<EmptyPokeBallEntity>(context) {

    override fun getTextureLocation(pEntity: EmptyPokeBallEntity): ResourceLocation {
        return PokeBallModelRepository.getModelTexture(pEntity.pokeBall)
    }

    override fun render(entity: EmptyPokeBallEntity, yaw: Float, partialTicks: Float, poseStack: PoseStack, buffer: MultiBufferSource, packedLight: Int) {
        val model = PokeBallModelRepository.getModel(entity.pokeBall).entityModel
        poseStack.pushPose()
        poseStack.scale(0.7F, 0.7F, 0.7F)
        val vertexConsumer = ItemRenderer.getFoilBufferDirect(buffer, model.renderType(getTextureLocation(entity)), false, false)
        model.setupAnim(entity, 0f, 0f, entity.tickCount + partialTicks, 0F, 0F)
        model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f)
        poseStack.popPose()
        super.render(entity, yaw, partialTicks, poseStack, buffer, packedLight)
    }
}