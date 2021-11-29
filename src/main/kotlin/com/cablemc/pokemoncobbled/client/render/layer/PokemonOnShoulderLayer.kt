package com.cablemc.pokemoncobbled.client.render.layer

import com.cablemc.pokemoncobbled.client.render.models.blockbench.repository.PokemonModelRepository
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.DataKeys
import com.cablemc.pokemoncobbled.common.util.isPokemonEntity
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.model.PlayerModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.layers.RenderLayer
import net.minecraft.world.entity.player.Player

class PokemonOnShoulderLayer<T : Player>(renderLayerParent: RenderLayerParent<T, PlayerModel<T>>) : RenderLayer<T, PlayerModel<T>>(renderLayerParent) {

    override fun render(
        pMatrixStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedLight: Int,
        pLivingEntity: T,
        pLimbSwing: Float,
        pLimbSwingAmount: Float,
        pPartialTicks: Float,
        pAgeInTicks: Float,
        pNetHeadYaw: Float,
        pHeadPitch: Float
    ) {
        this.render(pMatrixStack, pBuffer, pPackedLight, pLivingEntity, pLimbSwing, pLimbSwingAmount, pNetHeadYaw, pHeadPitch, true)
        this.render(pMatrixStack, pBuffer, pPackedLight, pLivingEntity, pLimbSwing, pLimbSwingAmount, pNetHeadYaw, pHeadPitch, false)
    }

    private fun render(
        pMatrixStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedLight: Int,
        pLivingEntity: T,
        pLimbSwing: Float,
        pLimbSwingAmount: Float,
        pNetHeadYaw: Float,
        pHeadPitch: Float,
        pLeftShoulder: Boolean
    ) {
        val compoundTag = if (pLeftShoulder) pLivingEntity.shoulderEntityLeft else pLivingEntity.shoulderEntityRight
        if (compoundTag.isPokemonEntity()) {
            pMatrixStack.pushPose()
            pMatrixStack.scale(0.5f, 0.5f, 0.5f)
            pMatrixStack.translate(
                if (pLeftShoulder) 0.7f.toDouble() else (-0.7f).toDouble(),
                if (pLivingEntity.isCrouching) -1.3 else -1.5,
                0.0
            )
            val pokemon = Pokemon().loadFromNBT(compoundTag.getCompound(DataKeys.POKEMON))
            val model = PokemonModelRepository.getModel(pokemon).entityModel
            val vertexConsumer = pBuffer.getBuffer(model.renderType(PokemonModelRepository.getModelTexture(pokemon)))
            val i = LivingEntityRenderer.getOverlayCoords(pLivingEntity, 0.0f)
            model.renderToBuffer(pMatrixStack, vertexConsumer, pPackedLight, i, 1.0f, 1.0f, 1.0f, 1.0f)
            pMatrixStack.popPose();
        }
    }

}