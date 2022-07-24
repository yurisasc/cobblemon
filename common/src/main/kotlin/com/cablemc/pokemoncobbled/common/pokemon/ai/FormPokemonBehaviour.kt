package com.cablemc.pokemoncobbled.common.pokemon.ai

import com.google.gson.annotations.SerializedName

/**
 * Form-specific AI behaviours. Any properties that are null in here should fall back to the same
 * non-null object in the root [PokemonBehaviour].
 *
 * @author Hiroku
 * @since July 15th, 2022
 */
class FormPokemonBehaviour {
    @Transient
    lateinit var parent: PokemonBehaviour

    @SerializedName("resting")
    private val _resting: RestBehaviour? = null

    val resting: RestBehaviour
        get() = _resting ?: parent.resting
}