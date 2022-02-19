package com.cablemc.pokemoncobbled.fabric

import com.cablemc.pokemoncobbled.common.PokemonCobbled.LOGGER
import net.fabricmc.api.ModInitializer

class FabricBootstrap : ModInitializer {
    override fun onInitialize() {
        LOGGER.info("Hello from Fabric!")
        PokemonCobbledFabric.initialize()
    }
}