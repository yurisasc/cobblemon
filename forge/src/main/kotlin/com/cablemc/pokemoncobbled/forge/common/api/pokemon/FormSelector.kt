package com.cablemc.pokemoncobbled.forge.common.api.pokemon

import com.cablemc.pokemoncobbled.forge.common.pokemon.FormData
import com.cablemc.pokemoncobbled.forge.common.pokemon.Species

/**
 * Selects which form the given properties will result in for the species.
 */
interface FormSelector {
    fun chooseForm(props: PokemonProperties, species: Species): FormData
}

object StandardFormSelector : FormSelector {
    override fun chooseForm(props: PokemonProperties, species: Species): FormData {
        return FormData()
    }

}