package com.cablemc.pokemoncobbled.fabric

import net.fabricmc.api.ModInitializer

class FabricBootstrap : ModInitializer {
    override fun onInitialize() {
        PokemonCobbledFabric.initialize()
    }
}