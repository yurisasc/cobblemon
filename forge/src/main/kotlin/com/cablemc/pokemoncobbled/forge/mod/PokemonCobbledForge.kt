package com.cablemc.pokemoncobbled.forge.mod

import com.cablemc.pokemoncobbled.common.CobbledConfiguredFeatures
import com.cablemc.pokemoncobbled.common.CobbledEntities.POKEMON_TYPE
import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.CobbledPlacements
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.PokemonCobbledModImplementation
import com.cablemc.pokemoncobbled.common.api.events.CobbledEvents
import com.cablemc.pokemoncobbled.common.api.reactive.Observable.Companion.filter
import com.cablemc.pokemoncobbled.common.api.reactive.Observable.Companion.takeFirst
import com.cablemc.pokemoncobbled.common.net.serverhandling.ServerPacketRegistrar
import com.cablemc.pokemoncobbled.common.permission.LuckPermsPermissionValidator
import com.cablemc.pokemoncobbled.forge.mod.net.CobbledForgeNetworkDelegate
import dev.architectury.event.events.common.LifecycleEvent
import dev.architectury.platform.forge.EventBuses
import net.minecraftforge.common.ForgeMod
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext

@Mod(PokemonCobbled.MODID)
class PokemonCobbledForge : PokemonCobbledModImplementation {
    init {
        with(FMLJavaModLoadingContext.get().modEventBus) {
            EventBuses.registerModEventBus(PokemonCobbled.MODID, this)

            CobbledEvents.ENTITY_ATTRIBUTE.pipe(filter { it.entityType == POKEMON_TYPE }, takeFirst())
                .subscribe {
                    it.attributeSupplier
                        .add(ForgeMod.ENTITY_GRAVITY.get())
                        .add(ForgeMod.NAMETAG_DISTANCE.get())
                        .add(ForgeMod.SWIM_SPEED.get())
                        .add(ForgeMod.REACH_DISTANCE.get())
                }

            addListener(this@PokemonCobbledForge::initialize)
            addListener(this@PokemonCobbledForge::serverInit)
            CobbledNetwork.networkDelegate = CobbledForgeNetworkDelegate
            ServerPacketRegistrar.registerHandlers()

            PokemonCobbled.preinitialize(this@PokemonCobbledForge)

            LifecycleEvent.SETUP.register {
                CobbledConfiguredFeatures.register()
                CobbledPlacements.register()
            }

            // TODO: Make listener for BiomeLoadingEvent to register feature to biomes
        }
    }

    fun serverInit(event: FMLDedicatedServerSetupEvent) {
    }

    fun initialize(event: FMLCommonSetupEvent) {
        PokemonCobbled.LOGGER.info("Initializing...")
        PokemonCobbled.initialize()
        CobbledNetwork.register()
        if (ModList.get().isLoaded("luckperms")) {
            PokemonCobbled.permissionValidator = LuckPermsPermissionValidator()
        }
    }

    override fun isModInstalled(id: String) = ModList.get().isLoaded(id)
}