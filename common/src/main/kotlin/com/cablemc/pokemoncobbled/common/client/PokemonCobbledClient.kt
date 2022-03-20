package com.cablemc.pokemoncobbled.common.client

import com.cablemc.pokemoncobbled.common.PokemonCobbled.LOGGER
import com.cablemc.pokemoncobbled.common.PokemonCobbledClientImplementation
import com.cablemc.pokemoncobbled.common.api.scheduling.ScheduledTaskTracker
import com.cablemc.pokemoncobbled.common.client.gui.PartyOverlay
import com.cablemc.pokemoncobbled.common.client.net.ClientPacketRegistrar
import com.cablemc.pokemoncobbled.common.client.render.layer.PokemonOnShoulderLayer
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.bedrock.animation.BedrockAnimationRepository
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.repository.PokeBallModelRepository
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.repository.PokemonModelRepository
import com.cablemc.pokemoncobbled.common.client.render.pokeball.PokeBallRenderer
import com.cablemc.pokemoncobbled.common.client.render.pokemon.PokemonRenderer
import com.cablemc.pokemoncobbled.common.client.storage.ClientStorageManager
import com.mojang.blaze3d.vertex.PoseStack
import dev.architectury.event.events.client.ClientPlayerEvent.CLIENT_PLAYER_JOIN
import dev.architectury.event.events.client.ClientPlayerEvent.CLIENT_PLAYER_QUIT
import net.minecraft.client.model.PlayerModel
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.world.entity.player.Player

object PokemonCobbledClient {
    lateinit var implementation: PokemonCobbledClientImplementation
    val storage = ClientStorageManager()

    lateinit var overlay: PartyOverlay

    fun initialize(implementation: PokemonCobbledClientImplementation) {
        LOGGER.info("Initializing Pokémon Cobbled client")
        this.implementation = implementation
        CLIENT_PLAYER_JOIN.register { storage.onLogin() }
        CLIENT_PLAYER_QUIT.register { ScheduledTaskTracker.clear() }

        overlay = PartyOverlay()
        ClientPacketRegistrar.registerHandlers()

        LOGGER.info("Initializing Pokémon models")
        PokemonModelRepository.init()
        LOGGER.info("Initializing PokéBall models")
        PokeBallModelRepository.init()
    }

    fun beforeChatRender(poseStack: PoseStack, partialDeltaTicks: Float) {
        overlay.onRenderGameOverlay(poseStack = poseStack, partialDeltaTicks = partialDeltaTicks)
    }

    fun onAddLayer(skinMap: Map<String, EntityRenderer<out Player>>?) {
        var renderer: LivingEntityRenderer<Player, PlayerModel<Player>>? = skinMap?.get("default") as LivingEntityRenderer<Player, PlayerModel<Player>>
        renderer?.addLayer(PokemonOnShoulderLayer(renderer))
        renderer = skinMap.get("slim") as LivingEntityRenderer<Player, PlayerModel<Player>>?
        renderer?.addLayer(PokemonOnShoulderLayer(renderer))
    }

    fun registerPokemonRenderer(context: EntityRendererProvider.Context): PokemonRenderer {
        LOGGER.info("Registering Pokémon renderer")
        PokemonModelRepository.initializeModels(context)
        return PokemonRenderer(context)
    }

    fun registerPokeBallRenderer(context: EntityRendererProvider.Context): PokeBallRenderer {
        LOGGER.info("Registering PokéBall renderer")
        PokeBallModelRepository.initializeModels(context)
        return PokeBallRenderer(context)
    }

    fun reloadCodedAssets() {
        BedrockAnimationRepository.clear()
        PokemonModelRepository.reload()
        PokeBallModelRepository.reload()
    }
}