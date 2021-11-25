package com.cablemc.pokemoncobbled.client.render.pokemon

import com.cablemc.pokemoncobbled.client.render.models.blockbench.repository.PokemonModelRepository
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer

class PokemonRenderer(
    context: EntityRendererProvider.Context
) : MobRenderer<PokemonEntity, EntityModel<PokemonEntity>>(context, null, 0.5f) {

    companion object {
        var DELTA_TICKS = 0F
    }

    // TODO register models in a more clearly defined place
    init {
        PokemonModelRepository.initializeModels(context)
    }

    override fun getTextureLocation(pEntity: PokemonEntity) = PokemonModelRepository.getModelTexture(pEntity.pokemon)
    override fun render(pEntity: PokemonEntity, pEntityYaw: Float, pPartialTicks: Float, pMatrixStack: PoseStack, pBuffer: MultiBufferSource, pPackedLight: Int) {
        DELTA_TICKS = pPartialTicks
        model = PokemonModelRepository.getModel(pEntity.pokemon).entityModel
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight)
    }
}