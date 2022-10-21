/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.forge.mod

import com.cablemc.pokemod.common.Pokemod
import com.cablemc.pokemod.common.PokemodConfiguredFeatures
import com.cablemc.pokemod.common.PokemodEntities.POKEMON_TYPE
import com.cablemc.pokemod.common.PokemodImplementation
import com.cablemc.pokemod.common.PokemodNetwork
import com.cablemc.pokemod.common.PokemodPlacements
import com.cablemc.pokemod.common.api.events.PokemodEvents
import com.cablemc.pokemod.common.api.reactive.Observable.Companion.filter
import com.cablemc.pokemod.common.api.reactive.Observable.Companion.takeFirst
import com.cablemc.pokemod.common.net.serverhandling.ServerPacketRegistrar
import com.cablemc.pokemod.forge.mod.net.PokemodForgeNetworkDelegate
import dev.architectury.event.events.common.LifecycleEvent
import dev.architectury.platform.forge.EventBuses
import net.minecraftforge.common.ForgeMod
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext

@Mod(Pokemod.MODID)
class PokemodForge : PokemodImplementation {
    init {
        with(FMLJavaModLoadingContext.get().modEventBus) {
            EventBuses.registerModEventBus(Pokemod.MODID, this)

            PokemodEvents.ENTITY_ATTRIBUTE.pipe(filter { it.entityType == POKEMON_TYPE }, takeFirst())
                .subscribe {
                    it.attributeSupplier
                        .add(ForgeMod.ENTITY_GRAVITY.get())
                        .add(ForgeMod.NAMETAG_DISTANCE.get())
                        .add(ForgeMod.SWIM_SPEED.get())
                        .add(ForgeMod.REACH_DISTANCE.get())
                }

            addListener(this@PokemodForge::initialize)
            addListener(this@PokemodForge::serverInit)
            PokemodNetwork.networkDelegate = PokemodForgeNetworkDelegate
            ServerPacketRegistrar.registerHandlers()

            Pokemod.preinitialize(this@PokemodForge)

            LifecycleEvent.SETUP.register {
                PokemodConfiguredFeatures.register()
                PokemodPlacements.register()
            }

            // TODO: Make listener for BiomeLoadingEvent to register feature to biomes
        }
    }

    fun serverInit(event: FMLDedicatedServerSetupEvent) {
    }

    fun initialize(event: FMLCommonSetupEvent) {
        Pokemod.LOGGER.info("Initializing...")
        Pokemod.initialize()
        PokemodNetwork.register()
        if (ModList.get().isLoaded("luckperms")) {
//            PokemonCobbled.permissionValidator = LuckPermsPermissionValidator()
        }
    }

    override fun isModInstalled(id: String) = ModList.get().isLoaded(id)
}