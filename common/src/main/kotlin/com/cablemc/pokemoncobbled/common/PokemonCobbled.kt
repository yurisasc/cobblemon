package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.api.pokeball.catching.calculators.CaptureCalculator
import com.cablemc.pokemoncobbled.common.api.pokeball.catching.calculators.Gen7CaptureCalculator
import com.cablemc.pokemoncobbled.common.battles.ShowdownThread
import com.cablemc.pokemoncobbled.common.battles.runner.ShowdownConnection

object PokemonCobbled {
    const val MODID = "pokemoncobbled"
    const val VERSION = "0.0.1"

    lateinit var showdown: ShowdownConnection
    var captureCalculator: CaptureCalculator = Gen7CaptureCalculator()
    var isDedicatedServer = false
    var showdownThread: ShowdownThread = ShowdownThread()

    fun initialize() {
        CobbledEntities.register()
        CobbledItems.register()
        CobbledSounds.register()
    }
}