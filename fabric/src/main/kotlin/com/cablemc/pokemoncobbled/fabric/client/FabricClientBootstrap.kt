package com.cablemc.pokemoncobbled.fabric.client

import net.fabricmc.api.ClientModInitializer

class FabricClientBootstrap: ClientModInitializer {
    override fun onInitializeClient() {
        PokemonCobbledClient
    }
}