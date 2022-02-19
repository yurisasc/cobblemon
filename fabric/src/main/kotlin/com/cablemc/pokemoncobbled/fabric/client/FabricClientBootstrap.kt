package com.cablemc.pokemoncobbled.fabric.client

import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import net.fabricmc.api.ClientModInitializer

class FabricClientBootstrap: ClientModInitializer {
    override fun onInitializeClient() {
        PokemonCobbledClient.initialize()
    }
}