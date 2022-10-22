/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.pokemon.ai

/**
 * Behavioural properties relating to a Pokémon's ability to look and move.
 *
 * @author Hiroku
 * @since July 30th, 2022
 */
class MoveBehaviour {
    val walk = WalkBehaviour()
    val swim = SwimBehaviour()
    val fly = FlyBehaviour()
    val canLook = true
}