/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.riding.seats.properties

import com.cobblemon.mod.common.api.net.Decodable
import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.api.riding.seats.Seat
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.entity.Entity
import net.minecraft.network.PacketByteBuf

class SeatDTO : Encodable, Decodable {

    lateinit var properties: SeatProperties
    var occupant: Int? = null

    constructor()
    constructor(properties: SeatProperties, occupant: Entity?) {
        this.properties = properties
        this.occupant = occupant?.id
    }

    override fun decode(buffer: PacketByteBuf) {
        this.properties = SeatProperties.decode(buffer)
        this.occupant = buffer.readNullable { _ -> buffer.readInt() }
    }

    override fun encode(buffer: PacketByteBuf) {
        this.properties.encode(buffer)
        buffer.writeNullable(this.occupant) { _, occupant -> buffer.writeInt(occupant) }
    }

    fun create(mount: PokemonEntity) : Seat {
        return Seat(mount, this.properties, if(this.occupant != null) mount.world.getEntityById(this.occupant!!) else null)
    }

}
