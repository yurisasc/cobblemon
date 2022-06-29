package com.cablemc.pokemoncobbled.common.pokemon.evolution

import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.EvolutionDisplay
import com.cablemc.pokemoncobbled.common.pokemon.FormData
import com.cablemc.pokemoncobbled.common.pokemon.Gender
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.Species

internal data class CobbledEvolutionDisplay(
    override val id: String,
    override val species: Species,
    override val aspects: Set<String>
) : EvolutionDisplay {

    constructor(id: String, pokemon: Pokemon) : this(id, pokemon.species, pokemon.aspects)

}