package com.cablemc.pokemoncobbled.fabric

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.PokemonCobbledModImplementation
import com.cablemc.pokemoncobbled.common.net.serverhandling.ServerPacketRegistrar
import com.cablemc.pokemoncobbled.fabric.net.CobbledFabricNetworkDelegate

object PokemonCobbledFabric : PokemonCobbledModImplementation {
    fun initialize() {
        CobbledNetwork.networkDelegate = CobbledFabricNetworkDelegate
        PokemonCobbled.preinitialize(this)
        PokemonCobbled.initialize()
        ServerPacketRegistrar.registerHandlers()
        CobbledNetwork.register()
    }
}