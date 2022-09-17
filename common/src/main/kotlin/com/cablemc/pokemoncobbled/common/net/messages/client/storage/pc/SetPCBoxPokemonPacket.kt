/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.net.messages.client.storage.pc

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.api.storage.pc.PCBox
import com.cablemc.pokemoncobbled.common.net.IntSize
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.readMapK
import com.cablemc.pokemoncobbled.common.util.readSizedInt
import com.cablemc.pokemoncobbled.common.util.writeMapK
import com.cablemc.pokemoncobbled.common.util.writeSizedInt
import net.minecraft.network.PacketByteBuf
import java.util.UUID

/**
 * Sets an entire box of Pokémon in the client side representation of a PC. This is used
 * during the initial sending of a PC's contents. It's better than sending hundreds of packets
 * for a full PC - this way it's one large-ish packet per box.
 *
 * Handled by [com.cablemc.pokemoncobbled.common.client.net.storage.pc.SetPCBoxPokemonHandler].
 *
 * @author Hiroku
 * @since June 18th, 2022
 */
class SetPCBoxPokemonPacket() : NetworkPacket {
    lateinit var storeID: UUID
    var boxNumber = 0
    var pokemon = mapOf<Int, Pokemon>()

    constructor(box: PCBox): this() {
        this.storeID = box.pc.uuid
        this.boxNumber = box.boxNumber
        this.pokemon = box.getNonEmptySlots()
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeUuid(storeID)
        buffer.writeSizedInt(IntSize.U_BYTE, boxNumber)
        buffer.writeMapK(map = pokemon) { (slot, pokemon) ->
            buffer.writeSizedInt(IntSize.U_BYTE, slot)
            pokemon.saveToBuffer(buffer, toClient = true)
        }
    }

    override fun decode(buffer: PacketByteBuf) {
        storeID = buffer.readUuid()
        boxNumber = buffer.readSizedInt(IntSize.U_BYTE)
        val pokemonMap = mutableMapOf<Int, Pokemon>()
        buffer.readMapK(map = pokemonMap) { buffer.readSizedInt(IntSize.U_BYTE) to Pokemon().loadFromBuffer(buffer) }
        pokemon = pokemonMap
    }
}