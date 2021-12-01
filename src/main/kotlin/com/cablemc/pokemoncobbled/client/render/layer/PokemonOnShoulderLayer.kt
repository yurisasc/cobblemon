package com.cablemc.pokemoncobbled.client.render.layer

import com.cablemc.pokemoncobbled.client.render.models.blockbench.repository.PokemonModelRepository
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.entity.EntityRegistry
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.NbtKeys
import com.cablemc.pokemoncobbled.common.util.isPokemonEntity
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.model.PlayerModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.client.renderer.entity.ParrotRenderer
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.layers.RenderLayer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.world.entity.EntityType
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
            val pokemon = Pokemon().load(compoundTag.getCompound(NbtKeys.POKEMON))
            val scale = pokemon.form.baseScale * pokemon.scaleModifier
            val width = pokemon.form.hitbox.width
            pMatrixStack.translate(
                if (pLeftShoulder) 0.7f.toDouble() - width / 2 else -0.7f.toDouble() + width / 2,
                (if (pLivingEntity.isCrouching) -1.3 else -1.5) * scale,
                0.0
            )
            pMatrixStack.scale(scale, scale, scale)
            val model = PokemonModelRepository.getModel(pokemon).entityModel
            val vertexConsumer = pBuffer.getBuffer(model.renderType(PokemonModelRepository.getModelTexture(pokemon)))
            val i = LivingEntityRenderer.getOverlayCoords(pLivingEntity, 0.0f)
            model.renderToBuffer(pMatrixStack, vertexConsumer, pPackedLight, i, 1.0f, 1.0f, 1.0f, 1.0f)
            pMatrixStack.popPose();
        }
    }

}