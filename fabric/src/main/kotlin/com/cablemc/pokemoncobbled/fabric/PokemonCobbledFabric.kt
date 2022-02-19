package com.cablemc.pokemoncobbled.fabric

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.PokemonCobbledModImplementation
import com.cablemc.pokemoncobbled.common.net.serverhandling.ServerPacketRegistrar
import com.cablemc.pokemoncobbled.fabric.net.CobbledFabricNetworkDelegate
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STARTED

object PokemonCobbledFabric : PokemonCobbledModImplementation {
    fun initialize() {
        CobbledNetwork.networkDelegate = CobbledFabricNetworkDelegate
        PokemonCobbled.preinitialize(this)
        PokemonCobbled.initialize()
        SERVER_STARTED.register { PokemonCobbled.onServerStarted(it) }

        ServerPacketRegistrar.register()
        ServerPacketRegistrar.registerHandlers()
        CobbledNetwork.register()
    }
}