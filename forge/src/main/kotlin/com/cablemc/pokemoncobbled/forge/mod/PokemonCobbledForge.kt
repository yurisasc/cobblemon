package com.cablemc.pokemoncobbled.forge.mod

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.PokemonCobbled.LOGGER
import com.cablemc.pokemoncobbled.common.PokemonCobbled.isDedicatedServer
import com.cablemc.pokemoncobbled.common.PokemonCobbledModImplementation
import com.cablemc.pokemoncobbled.common.api.scheduling.ScheduledTaskListener
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.bedrock.animation.BedrockAnimationRepository
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.repository.PokeBallModelRepository
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.repository.PokemonModelRepository
import com.cablemc.pokemoncobbled.common.net.serverhandling.ServerPacketRegistrar
import com.cablemc.pokemoncobbled.common.util.ifClient
import com.cablemc.pokemoncobbled.common.util.ifServer
import com.cablemc.pokemoncobbled.forge.common.CommandRegistrar
import com.cablemc.pokemoncobbled.forge.mod.config.CobbledConfig
import com.cablemc.pokemoncobbled.forge.mod.net.CobbledForgeNetworkDelegate
import net.minecraftforge.client.event.ModelBakeEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.server.ServerStartingEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext

object PokemonCobbledForge : PokemonCobbledModImplementation {
    init {
        with(FMLJavaModLoadingContext.get().modEventBus) {
            addListener(this@PokemonCobbledForge::initialize)
//            addListener(this@PokemonCobbledMod::on)
            addListener(this@PokemonCobbledForge::onBake)
            addListener(PokemonCobbledClient::onAddLayer)
            CobbledNetwork.networkDelegate = CobbledForgeNetworkDelegate
            PokemonCobbled.initialize()
            PokemonCobbled.preinitialize(this@PokemonCobbledForge)
        }

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CobbledConfig.spec)
    }

    fun initialize(event: FMLCommonSetupEvent) {
        LOGGER.info("Initializing...")

        PokemonCobbled.initialize()

        event.enqueueWork {
            ifClient { PokemonCobbledClient.initialize() }
            ifServer {
                isDedicatedServer = true
            }
            ServerPacketRegistrar.register()
            ServerPacketRegistrar.registerHandlers()
            CobbledNetwork.register()
        }

        MinecraftForge.EVENT_BUS.register(CommandRegistrar)
        MinecraftForge.EVENT_BUS.register(ScheduledTaskListener)
        MinecraftForge.EVENT_BUS.register(this)
    }

    fun onBake(event: ModelBakeEvent) {
        BedrockAnimationRepository.clear()
        PokemonModelRepository.init()
        PokeBallModelRepository.init()
        PokemonCobbledClient.registerRenderers()
    }

    @SubscribeEvent
    fun onServerStarting(event: ServerStartingEvent) {
        PokemonCobbled.onServerStarted(event.server)
    }

//    fun on(event: EntityAttributeCreationEvent) {
//        EntityRegistry.registerAttributes(event)
//    }
}