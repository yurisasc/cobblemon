package com.cablemc.pokemoncobbled.fabric

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.PokemonCobbledModImplementation
import com.cablemc.pokemoncobbled.common.net.serverhandling.ServerPacketRegistrar
import com.cablemc.pokemoncobbled.fabric.net.CobbledFabricNetworkDelegate
import net.fabricmc.loader.api.FabricLoader

object PokemonCobbledFabric : PokemonCobbledModImplementation {
    override fun isModInstalled(id: String) = FabricLoader.getInstance().isModLoaded(id)
    fun initialize() {
        CobbledNetwork.networkDelegate = CobbledFabricNetworkDelegate
        PokemonCobbled.cobbledFeatures = FabricFeatures
        PokemonCobbled.cobbledConfiguredFeatures = FabricConfiguredFeatures
        PokemonCobbled.preinitialize(this)
        FabricFeatures.register()
        PokemonCobbled.initialize()
        ServerPacketRegistrar.registerHandlers()
        CobbledNetwork.register()
    }
}