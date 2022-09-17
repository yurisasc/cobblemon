/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

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

    @SerializedName("moving")
    private val _moving: MoveBehaviour? = null

    val resting: RestBehaviour
        get() = _resting ?: parent.resting

    val moving: MoveBehaviour
        get() = _moving ?: parent.moving
}