package com.cablemc.pokemoncobbled.common.pokemon

import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.api.pokemon.stats.Stats

class Pokemon {
    var species: Species = PokemonSpecies.EEVEE
    var form: PokemonForm = species.forms.first()
        private set

    var health = 10
    var level = 5
    var stats = mutableMapOf(
        Stats.HP to 20,
        Stats.ATTACK to 10,
        Stats.DEFENCE to 10,
        Stats.SPECIAL_ATTACK to 10,
        Stats.SPECIAL_DEFENCE to 10,
        Stats.SPEED to 15
    )
}