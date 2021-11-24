package com.cablemc.pokemoncobbled.client.render.pokemon

import com.cablemc.pokemoncobbled.client.render.models.blockbench.repository.PokemonModelRepository
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation

class PokemonRenderer(
    context: EntityRendererProvider.Context
) : MobRenderer<PokemonEntity, EntityModel<PokemonEntity>>(context, null, 0.5f) {

    init {
        PokemonModelRepository.initializeModels(context)
    }

    override fun getTextureLocation(pEntity: PokemonEntity): ResourceLocation {
        // TODO: Remove when pokemon entity development is more matured
        val pokemon = if (pEntity.uuid.leastSignificantBits % 2 == 0L) {
            Pokemon().apply { this.species = PokemonSpecies.EEVEE }
        }
        else {
            Pokemon().apply { this.species = PokemonSpecies.BULBASAUR }
        }
        return PokemonModelRepository.getModelTexture(pokemon)
    }

    override fun render(
        pEntity: PokemonEntity,
        pEntityYaw: Float,
        pPartialTicks: Float,
        pMatrixStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedLight: Int
    ) {
        // TODO: Remove when pokemon entity development is more matured
        if (pEntity.uuid.leastSignificantBits % 2 == 0L) {
            model = PokemonModelRepository.getModel(Pokemon().apply { this.species = PokemonSpecies.EEVEE }).entityModel
        }
        else {
            model = PokemonModelRepository.getModel(Pokemon().apply { this.species = PokemonSpecies.BULBASAUR }).entityModel
        }
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight)
    }

}