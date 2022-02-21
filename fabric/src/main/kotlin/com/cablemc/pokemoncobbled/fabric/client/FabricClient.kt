package com.cablemc.pokemoncobbled.fabric.client

import com.cablemc.pokemoncobbled.common.CobbledEntities
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.bedrock.animation.BedrockAnimationRepository
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.repository.PokeBallModelRepository
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.repository.PokemonModelRepository
import com.cablemc.pokemoncobbled.common.client.render.pokeball.PokeBallRenderer
import com.cablemc.pokemoncobbled.common.client.render.pokemon.PokemonRenderer
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry

object FabricClient {
    fun beforeFirstResourceManagerReload() {
        PokemonModelRepository.init()
        PokeBallModelRepository.init()
        BedrockAnimationRepository.clear()

        EntityRendererRegistry.register(CobbledEntities.POKEMON_TYPE) {
            PokemonModelRepository.initializeModels(it)
            return@register PokemonRenderer(it)
        }
        EntityRendererRegistry.register(CobbledEntities.EMPTY_POKEBALL_TYPE) {
            PokeBallModelRepository.initializeModels(it)
            return@register PokeBallRenderer(it)
        }

    }
}