package com.cablemc.pokemoncobbled.mod

import com.cablemc.pokemoncobbled.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.CommandRegistrar
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.entity.EntityRegistry
import com.cablemc.pokemoncobbled.common.event.InteractListener
import com.cablemc.pokemoncobbled.common.item.ItemRegistry
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.EntityAttributeCreationEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fmlserverevents.FMLServerStartingEvent
import org.apache.logging.log4j.LogManager
import thedarkcolour.kotlinforforge.forge.MOD_CONTEXT

@Mod(PokemonCobbled.MODID)
object PokemonCobbledMod {
    val LOGGER = LogManager.getLogger()

    init {
        with(MOD_CONTEXT.getKEventBus()) {
            addListener(this@PokemonCobbledMod::initialize)
            addListener(this@PokemonCobbledMod::on)
            addListener(PokemonCobbledClient::onAddLayer)
            EntityRegistry.register(this)
            ItemRegistry.register(this)
        }
    }

    fun initialize(event: FMLCommonSetupEvent) {
        LOGGER.info("Initializing...")
        event.enqueueWork {
            // Load spawns, for instance
        }

        // Touching this object loads them and the stats. Probably better to use lateinit and a dedicated .register for this and stats
        LOGGER.info("Loaded ${PokemonSpecies.count()} Pokémon species.")

        event.enqueueWork {
            DistExecutor.safeRunWhenOn(Dist.CLIENT) { DistExecutor.SafeRunnable { PokemonCobbledClient.initialize() } }
        }

        MinecraftForge.EVENT_BUS.register(CommandRegistrar)
        MinecraftForge.EVENT_BUS.register(InteractListener)
    }

    @SubscribeEvent
    fun onServerStarting(event: FMLServerStartingEvent) {
        // do something when the server starts

    }

    fun on(event: EntityAttributeCreationEvent) {
        EntityRegistry.registerAttributes(event)
    }

//    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
//    // Event bus for receiving Registry Events)
//    @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
//    object RegistryEvents {
//        @SubscribeEvent
//        fun onBlocksRegistry(blockRegistryEvent: Register<Block?>?) {
//            // register a new block here
//            LOGGER.info("HELLO from Register Block")
//        }
//    }
}