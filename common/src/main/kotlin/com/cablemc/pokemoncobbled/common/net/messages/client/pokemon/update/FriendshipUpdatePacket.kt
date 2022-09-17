/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update

import com.cablemc.pokemoncobbled.common.net.IntSize
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

class FriendshipUpdatePacket() : IntUpdatePacket() {
    constructor(pokemon: Pokemon, value: Int) : this() {
        this.setTarget(pokemon)
        this.value = value
    }

    override fun getSize() = IntSize.U_BYTE
    override fun set(pokemon: Pokemon, value: Int) {
        pokemon.setFriendship(value)
    }
}