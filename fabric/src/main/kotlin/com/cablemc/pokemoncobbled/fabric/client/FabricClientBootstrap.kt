package com.cablemc.pokemoncobbled.fabric.client

import com.cablemc.pokemoncobbled.common.CobbledEntities.EMPTY_POKEBALL_TYPE
import com.cablemc.pokemoncobbled.common.CobbledEntities.POKEMON_TYPE
import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.PokemonCobbled.LOGGER
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.bedrock.animation.BedrockAnimationRepository
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.repository.PokeBallModelRepository
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.repository.PokemonModelRepository
import com.cablemc.pokemoncobbled.common.client.render.pokeball.PokeBallRenderer
import com.cablemc.pokemoncobbled.common.client.render.pokemon.PokemonRenderer
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.ResourceManager

class FabricClientBootstrap: ClientModInitializer {
    override fun onInitializeClient() {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(object : SimpleSynchronousResourceReloadListener {
            override fun getFabricId() = cobbledResource("resources")
            override fun onResourceManagerReload(resourceManager: ResourceManager) {
                loadAssets()
            }
        })
        PokemonCobbledClient.initialize()

        loadAssets()
        EntityRendererRegistry.register(POKEMON_TYPE) {
            PokemonModelRepository.initializeModels(it)
            return@register PokemonRenderer(it)
        }
        EntityRendererRegistry.register(EMPTY_POKEBALL_TYPE) {
            PokeBallModelRepository.initializeModels(it)
            return@register PokeBallRenderer(it)
        }
        LOGGER.info("Registered entity renderers")

        CobbledNetwork.register()
    }

    fun loadAssets() {
        LOGGER.info("Loading models")
        BedrockAnimationRepository.clear()
        PokemonModelRepository.init()
        LOGGER.info("Pokémon models loaded")
        PokeBallModelRepository.init()
        LOGGER.info("PokéBall models loaded")
    }
}