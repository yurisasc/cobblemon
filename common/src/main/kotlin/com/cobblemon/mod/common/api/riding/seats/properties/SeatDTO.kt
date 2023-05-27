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
        return Seat(this.properties, if(this.occupant != null) mount.world.getEntityById(this.occupant!!) else null)
    }

}
