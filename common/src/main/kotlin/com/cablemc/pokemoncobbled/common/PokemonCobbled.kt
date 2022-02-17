package com.cablemc.pokemoncobbled.common

object PokemonCobbled {
    const val MODID = "pokemoncobbled"
    const val VERSION = "0.0.1"

    fun initialize() {
        CobbledSounds.register()
    }
}