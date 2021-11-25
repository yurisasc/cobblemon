package com.cablemc.pokemoncobbled.client.render.layer

import com.cablemc.pokemoncobbled.client.render.models.blockbench.repository.PokemonModelRepository
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
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
        pMatrixStack.pushPose()
        val x = 0.4
        val y = if(pLivingEntity.isCrouching) -0.798 else -0.95
        pMatrixStack.translate(x, y, 0.0)
        pMatrixStack.scale(0.65f, 0.65f, 0.65f)

        val pokemon = Pokemon().apply { this.species = PokemonSpecies.EEVEE }
        val model = PokemonModelRepository.getModel(pokemon).entityModel
        val vertexConsumer = pBuffer.getBuffer(model.renderType(PokemonModelRepository.getModelTexture(pokemon)))

        val i = LivingEntityRenderer.getOverlayCoords(pLivingEntity, 0.0f)
        model.renderToBuffer(pMatrixStack, vertexConsumer, pPackedLight, i, 1.0f, 1.0f, 1.0f, 1.0f)
        pMatrixStack.popPose();
    }

}