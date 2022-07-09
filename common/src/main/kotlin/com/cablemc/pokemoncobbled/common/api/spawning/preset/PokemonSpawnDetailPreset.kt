package com.cablemc.pokemoncobbled.common.api.spawning.preset

import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.cablemc.pokemoncobbled.common.api.spawning.detail.PokemonSpawnDetail
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnDetail

/**
 * A [SpawnDetailPreset] that has extra fields that can apply specifically to [PokemonSpawnDetail]s.
 *
 * @author Hiroku
 * @since July 8th, 2022
 */
class PokemonSpawnDetailPreset : SpawnDetailPreset() {
    companion object {
        const val NAME = "pokemon"
    }

    var pokemon: PokemonProperties? = null
    var levelRange: IntRange? = null

    override fun apply(spawnDetail: SpawnDetail) {
        super.apply(spawnDetail)
        if (spawnDetail is PokemonSpawnDetail) {
            val pokemon = pokemon
            if (pokemon != null) {
                spawnDetail.pokemon = PokemonProperties.parse(spawnDetail.pokemon.originalString + " " + pokemon.originalString)
            }
            if (levelRange != null) {
                spawnDetail.levelRange = levelRange
            }
        }
    }
}