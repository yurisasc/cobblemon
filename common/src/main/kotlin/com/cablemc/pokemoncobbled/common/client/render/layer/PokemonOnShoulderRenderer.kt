package com.cablemc.pokemoncobbled.common.client.render.layer

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.PoseableEntityModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.repository.PokemonModelRepository
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.DataKeys
import com.cablemc.pokemoncobbled.common.util.isPokemonEntity
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.LivingEntityRenderer
import net.minecraft.client.render.entity.feature.FeatureRenderer
import net.minecraft.client.render.entity.feature.FeatureRendererContext
import net.minecraft.client.render.entity.model.PlayerEntityModel
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerEntity

class PokemonOnShoulderRenderer<T : PlayerEntity>(renderLayerParent: FeatureRendererContext<T, PlayerEntityModel<T>>) : FeatureRenderer<T, PlayerEntityModel<T>>(renderLayerParent) {
    override fun render(
        pMatrixStack: MatrixStack,
        pBuffer: VertexConsumerProvider,
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
        pMatrixStack: MatrixStack,
        pBuffer: VertexConsumerProvider,
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
            pMatrixStack.push()
            val pokemon = Pokemon().loadFromNBT(compoundTag.getCompound(DataKeys.POKEMON))
            val scale = pokemon.form.baseScale * pokemon.scaleModifier
            val width = pokemon.form.hitbox.width
            val offset = width / 2 - 0.7
            pMatrixStack.translate(
                if (pLeftShoulder) -offset else offset,
                (if (pLivingEntity.isSneaking) -1.3 else -1.5) * scale,
                0.0
            )
            pMatrixStack.scale(scale, scale, scale)
            val model = PokemonModelRepository.getEntityModel(pokemon.species, pokemon.aspects)
            val vertexConsumer = pBuffer.getBuffer(model.getLayer(PokemonModelRepository.getModelTexture(pokemon.species, pokemon.aspects)))
            val i = LivingEntityRenderer.getOverlay(pLivingEntity, 0.0f)
            model.setupAnimStateless(
                poseType = if (pLeftShoulder) PoseType.SHOULDER_LEFT else PoseType.SHOULDER_RIGHT,
                headYaw = pNetHeadYaw,
                headPitch = pHeadPitch,
                limbSwing = pLimbSwing,
                limbSwingAmount = pLimbSwingAmount,
                ageInTicks = pLivingEntity.age.toFloat()
            )
            model.render(pMatrixStack, vertexConsumer, pPackedLight, i, 1.0f, 1.0f, 1.0f, 1.0f)
            pMatrixStack.pop();
        }
    }
}