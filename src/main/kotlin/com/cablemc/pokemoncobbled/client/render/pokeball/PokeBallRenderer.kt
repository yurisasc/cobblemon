package com.cablemc.pokemoncobbled.client.render.pokeball

import com.cablemc.pokemoncobbled.client.render.models.blockbench.repository.PokeBallModelRepository
import com.cablemc.pokemoncobbled.common.entity.pokeball.PokeBallEntity
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Quaternion
import com.mojang.math.Vector3f
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth

class PokeBallRenderer<T : PokeBallEntity>(context: EntityRendererProvider.Context) : EntityRenderer<T>(context) {

    init {
        PokeBallModelRepository.initializeModels(context)
    }

    override fun getTextureLocation(pEntity: T): ResourceLocation {
        return PokeBallModelRepository.getModelTexture(pEntity.pokeBall)
    }

    override fun render(
        pEntity: T,
        pEntityYaw: Float,
        pPartialTicks: Float,
        pMatrixStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedLight: Int
    ) {
        val model = PokeBallModelRepository.getModel(pEntity.pokeBall).entityModel
        pMatrixStack.pushPose()
        pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(pPartialTicks, pEntity.yRotO, pEntity.yRot) - 90.0f))
        pMatrixStack.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.xRot) + 90.0f))
        val vertexconsumer = ItemRenderer.getFoilBufferDirect(pBuffer, model.renderType(getTextureLocation(pEntity)), false, false)
        model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f)
        pMatrixStack.popPose()
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight)
    }

}