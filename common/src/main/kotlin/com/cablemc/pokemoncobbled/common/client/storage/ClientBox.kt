/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.client.storage

import com.cablemc.pokemoncobbled.common.api.storage.pc.POKEMON_PER_BOX
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

class ClientBox : Iterable<Pokemon?> {
    val slots = MutableList<Pokemon?>(POKEMON_PER_BOX) { null }
    override fun iterator() = slots.iterator()
}