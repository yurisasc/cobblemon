package com.cobblemon.mod.common.api.pokemon.species.internal

import com.cobblemon.mod.common.api.pokemon.evolution.Evolution
import com.cobblemon.mod.common.api.pokemon.evolution.PreEvolution
import com.cobblemon.mod.common.api.pokemon.species.Species
import com.mojang.serialization.Codec
import java.util.*

/**
 * Intermediate for [Species] data.
 */
internal data class EvolutionData(
    var evolutions: Set<Evolution>,
    var preEvolution: Optional<PreEvolution>,
) {

    companion object {

        val CODEC: Codec<EvolutionData> = TODO("Not yet implemented")

    }

}
