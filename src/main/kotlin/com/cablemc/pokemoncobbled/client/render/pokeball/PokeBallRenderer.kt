package com.cablemc.pokemoncobbled.client.render.pokeball

import com.cablemc.pokemoncobbled.client.render.models.blockbench.repository.PokeBallModelRepository
import com.cablemc.pokemoncobbled.common.entity.pokeball.EmptyPokeBallEntity
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation

class PokeBallRenderer(context: EntityRendererProvider.Context) : EntityRenderer<EmptyPokeBallEntity>(context) {

    init {
        PokeBallModelRepository.initializeModels(context)
    }

    override fun getTextureLocation(pEntity: EmptyPokeBallEntity): ResourceLocation {
        return PokeBallModelRepository.getModelTexture(pEntity.pokeBall)
    }

    override fun render(
        pEntity: EmptyPokeBallEntity,
        pEntityYaw: Float,
        pPartialTicks: Float,
        pMatrixStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedLight: Int
    ) {
        val model = PokeBallModelRepository.getModel(pEntity.pokeBall).entityModel
        pMatrixStack.pushPose()
        pMatrixStack.scale(0.7F, 0.7F, 0.7F)
        val vertexconsumer = ItemRenderer.getFoilBufferDirect(pBuffer, model.renderType(getTextureLocation(pEntity)), false, false)
        model.setupAnim(pEntity, 0f, 0f, pEntity.tickCount + pPartialTicks, 0F, 0F)
        model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f)
        pMatrixStack.popPose()
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight)
    }

}