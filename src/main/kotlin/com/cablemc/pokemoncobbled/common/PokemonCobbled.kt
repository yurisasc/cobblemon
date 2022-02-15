package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.api.spawning.SpawnerManager
import com.cablemc.pokemoncobbled.common.api.spawning.context.AreaContextResolver
import com.cablemc.pokemoncobbled.common.api.spawning.prospecting.SpawningProspector
import com.cablemc.pokemoncobbled.common.api.storage.PokemonStoreManager
import com.cablemc.pokemoncobbled.common.spawning.CobbledSpawningProspector
import com.cablemc.pokemoncobbled.common.spawning.CobbledWorldSpawnerManager

object PokemonCobbled {
    const val MODID = "pokemoncobbled"
    const val VERSION = "0.0.1"

    var prospector: SpawningProspector = CobbledSpawningProspector
    var areaContextResolver: AreaContextResolver = object : AreaContextResolver {}

    val spawnerManagers = mutableListOf<SpawnerManager>(CobbledWorldSpawnerManager)
    var storage = PokemonStoreManager()
}