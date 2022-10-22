/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.net.messages.client.pokemon.update

import com.cablemc.pokemod.common.api.moves.MoveSet
import com.cablemc.pokemod.common.pokemon.Pokemon
import net.minecraft.network.PacketByteBuf

class MoveSetUpdatePacket internal constructor(): SingleUpdatePacket<MoveSet>(MoveSet()) {

    constructor(
        pokemon: Pokemon,
        moveSet: MoveSet
    ): this() {
        setTarget(pokemon)
        value = moveSet
    }

    override fun encodeValue(buffer: PacketByteBuf, value: MoveSet) {
        value.saveToBuffer(buffer)
    }

    override fun decodeValue(buffer: PacketByteBuf): MoveSet {
        return MoveSet().loadFromBuffer(buffer)
    }

    override fun set(pokemon: Pokemon, value: MoveSet) {
        pokemon.moveSet.copyFrom(value)
    }
}