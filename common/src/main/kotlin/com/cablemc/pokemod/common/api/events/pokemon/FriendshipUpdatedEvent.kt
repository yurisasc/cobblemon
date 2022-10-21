/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.events.pokemon

import com.cablemc.pokemod.common.pokemon.Pokemon

/**
 * Event that is fired when a player owned Pokémon has its happiness changed
 *
 * @author Blue
 * @since 2022-02-08
 */
data class FriendshipUpdatedEvent(
    val pokemon: Pokemon,
    var newFriendship: Int
)