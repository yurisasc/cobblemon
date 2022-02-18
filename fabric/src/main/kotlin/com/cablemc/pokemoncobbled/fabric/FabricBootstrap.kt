package com.cablemc.pokemoncobbled.fabric

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

class FabricBootstrap : ModInitializer {

    val LOGGER = LoggerFactory.getLogger(PokemonCobbled.MODID)

    override fun onInitialize() {
        LOGGER.info("Hello from Fabric!")
        PokemonCobbledMod
    }
}