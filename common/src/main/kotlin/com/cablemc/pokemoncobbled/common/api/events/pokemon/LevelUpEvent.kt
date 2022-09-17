/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.api.events.pokemon

import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

/**
 * Event fired when a Pokémon levels up. The new level that it will reach is changeable.
 *
 * @author Hiroku
 * @since August 5th, 2022
 */
class LevelUpEvent(val pokemon: Pokemon, val oldLevel: Int, var newLevel: Int)