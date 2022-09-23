/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update

import com.cablemc.pokemoncobbled.common.api.moves.BenchedMoves
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.network.PacketByteBuf

class BenchedMovesUpdatePacket() : SingleUpdatePacket<BenchedMoves>(BenchedMoves()) {
    constructor(pokemon: Pokemon, value: BenchedMoves) : this() {
        setTarget(pokemon)
        this.value = value
    }

    override fun encodeValue(buffer: PacketByteBuf, value: BenchedMoves) {
        value.saveToBuffer(buffer)
    }

    override fun decodeValue(buffer: PacketByteBuf) = value.loadFromBuffer(buffer)

    override fun set(pokemon: Pokemon, value: BenchedMoves) {
        pokemon.benchedMoves.doThenEmit {
            pokemon.benchedMoves.clear()
            pokemon.benchedMoves.addAll(value)
        }
    }
}