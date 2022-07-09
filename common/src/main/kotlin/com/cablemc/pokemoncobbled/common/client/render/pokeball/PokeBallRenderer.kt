package com.cablemc.pokemoncobbled.common.client.render.pokeball

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.PoseableEntityModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.repository.PokeBallModelRepository
import com.cablemc.pokemoncobbled.common.entity.pokeball.EmptyPokeBallEntity
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.item.ItemRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

class PokeBallRenderer(context: EntityRendererFactory.Context) : EntityRenderer<EmptyPokeBallEntity>(context) {

    override fun getTexture(pEntity: EmptyPokeBallEntity): Identifier {
        return PokeBallModelRepository.getModelTexture(pEntity.pokeBall)
    }

    override fun render(entity: EmptyPokeBallEntity, yaw: Float, partialTicks: Float, poseStack: MatrixStack, buffer: VertexConsumerProvider, packedLight: Int) {
        val model = PokeBallModelRepository.getModel(entity.pokeBall).entityModel as PoseableEntityModel<EmptyPokeBallEntity>
        poseStack.push()
        poseStack.scale(0.7F, 0.7F, 0.7F)
        val vertexConsumer = ItemRenderer.getDirectItemGlintConsumer(buffer, model.getLayer(getTexture(entity)), false, false)
        model.setAngles(entity, 0f, 0f, entity.age + partialTicks, 0F, 0F)
        model.render(poseStack, vertexConsumer, packedLight, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f)
        poseStack.pop()
        super.render(entity, yaw, partialTicks, poseStack, buffer, packedLight)
    }
}