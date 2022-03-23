package com.cablemc.pokemoncobbled.common.api.pokemon.evolution

import com.cablemc.pokemoncobbled.common.pokemon.FormData
import com.cablemc.pokemoncobbled.common.pokemon.Species
import com.cablemc.pokemoncobbled.common.pokemon.evolution.CobbledPreEvolution

/**
 * TODO
 *
 * @author Licious
 * @since March 22nd, 2022
 */
interface PreEvolution {

    val species: Species

    val form: FormData

    companion object {

        fun of(species: Species, form: FormData = species.forms.first()): PreEvolution = CobbledPreEvolution(species.name, form.name)

    }

}