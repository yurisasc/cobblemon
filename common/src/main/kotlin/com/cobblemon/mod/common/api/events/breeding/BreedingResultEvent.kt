package com.cobblemon.mod.common.api.events.breeding

import com.cobblemon.mod.common.api.pokemon.breeding.BreedingResult
import com.cobblemon.mod.common.pokemon.Pokemon

data class BreedingResultEvent(
    val breedingResult: BreedingResult,
    val mother: Pokemon,
    val father: Pokemon
)