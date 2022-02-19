package com.cablemc.pokemoncobbled.forge.mod

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.PokemonCobbled.LOGGER
import com.cablemc.pokemoncobbled.common.PokemonCobbled.isDedicatedServer
import com.cablemc.pokemoncobbled.common.PokemonCobbledClientImplementation
import com.cablemc.pokemoncobbled.common.PokemonCobbledModImplementation
import com.cablemc.pokemoncobbled.common.api.scheduling.ScheduledTaskListener
import com.cablemc.pokemoncobbled.common.api.storage.PokemonStoreManager
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.bedrock.animation.BedrockAnimationRepository
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.repository.PokeBallModelRepository
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.repository.PokemonModelRepository
import com.cablemc.pokemoncobbled.common.spawning.SpawnerManager
import com.cablemc.pokemoncobbled.common.util.getServer
import com.cablemc.pokemoncobbled.common.util.ifClient
import com.cablemc.pokemoncobbled.common.util.ifServer
import com.cablemc.pokemoncobbled.common.api.moves.Moves
import com.cablemc.pokemoncobbled.common.client.keybind.CobbledKeybinds
import com.cablemc.pokemoncobbled.forge.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.forge.common.CommandRegistrar
import com.cablemc.pokemoncobbled.common.net.serverhandling.ServerPacketRegistrar
import com.cablemc.pokemoncobbled.forge.mod.config.CobbledConfig
import com.cablemc.pokemoncobbled.forge.mod.net.CobbledForgeNetworkDelegate
import dev.architectury.platform.forge.EventBuses
import dev.architectury.platform.forge.EventBuses
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import net.minecraftforge.client.event.ModelBakeEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.server.ServerStartingEvent
import net.minecraftforge.eventbus.api.BusBuilder
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext

object PokemonCobbledMod : PokemonCobbledModImplementation {
    val EVENT_BUS = BusBuilder.builder().build()

    init {
        with(FMLJavaModLoadingContext.get().modEventBus) {
            addListener(this@PokemonCobbledMod::initialize)
//            addListener(this@PokemonCobbledMod::on)
            addListener(this@PokemonCobbledMod::onBake)
            addListener(PokemonCobbledClient::onAddLayer)
            CobbledKeybinds
            PokemonCobbled.initialize()
            PokemonCobbled.preinitialize(this@PokemonCobbledMod)
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
            EVENT_BUS.register(ServerPacketRegistrar)
            ServerPacketRegistrar.registerHandlers()
            CobbledNetwork.register()
        }

        MinecraftForge.EVENT_BUS.register(CommandRegistrar)
        MinecraftForge.EVENT_BUS.register(PokemonStoreManager)
        MinecraftForge.EVENT_BUS.register(ScheduledTaskListener)
        MinecraftForge.EVENT_BUS.register(this)
        MinecraftForge.EVENT_BUS.register(SpawnerManager)
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

    fun getLevel(dimension: ResourceKey<Level>): Level? {
        return if (isDedicatedServer) {
            getServer()?.getLevel(dimension)
        } else {
            val mc = Minecraft.getInstance()
            if (mc.singleplayerServer != null) {
                mc.singleplayerServer!!.getLevel(dimension)
            } else if (mc.level?.dimension() == dimension) {
                mc.level
            } else {
                null
            }
        }
    }

    override val client: PokemonCobbledClientImplementation
        get() = TODO("Not yet implemented")

    override val networkDelegate = CobbledForgeNetworkDelegate
}