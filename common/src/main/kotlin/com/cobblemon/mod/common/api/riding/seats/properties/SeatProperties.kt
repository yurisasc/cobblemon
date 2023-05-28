package com.cobblemon.mod.common.api.riding.seats.properties

import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.api.riding.seats.Seat
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.math.Vec3d

/**
 * Seat Properties are responsible for the base information that would then be used to construct a Seat on an entity.
 *
 * @since 1.5.0
 */
data class SeatProperties(
    val driver: Boolean,
    val offset: Vec3d
) : Encodable {

    fun create(mount: PokemonEntity): Seat {
        return Seat(mount, this)
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeBoolean(this.driver)
        buffer.writeDouble(this.offset.x)
        buffer.writeDouble(this.offset.y)
        buffer.writeDouble(this.offset.z)
    }

    companion object {

        fun decode(buffer: PacketByteBuf) : SeatProperties {
            return SeatProperties(
                buffer.readBoolean(),
                Vec3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble())
            )
        }

    }

}
