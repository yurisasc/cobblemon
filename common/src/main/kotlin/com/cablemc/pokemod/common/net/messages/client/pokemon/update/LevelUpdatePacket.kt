/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.net.messages.client.pokemon.update

import com.cablemc.pokemod.common.net.IntSize
import com.cablemc.pokemod.common.pokemon.Pokemon
class LevelUpdatePacket() : IntUpdatePacket() {
    constructor(pokemon: Pokemon, value: Int): this() {
        this.setTarget(pokemon)
        this.value = value
    }

    override fun getSize() = IntSize.U_SHORT
    override fun set(pokemon: Pokemon, value: Int) { pokemon.level = value }
}