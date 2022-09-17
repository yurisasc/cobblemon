/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.pokemon.ai

/**
 * Collection of all AI properties defineable at the species level of a Pokémon.
 *
 * @author Hiroku
 * @since July 15th, 2022
 */
open class PokemonBehaviour {
    val resting = RestBehaviour()
    var moving = MoveBehaviour()
}