package com.cobblemon.mod.common.api.events.breeding

import com.cobblemon.mod.common.pokemon.Pokemon

data class BreedingEligibilityEvent(
    val canBreed: Boolean,
    val mother: Pokemon,
    val father: Pokemon
)