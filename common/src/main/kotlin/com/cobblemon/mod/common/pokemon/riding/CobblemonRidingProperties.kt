package com.cobblemon.mod.common.pokemon.riding

import com.cobblemon.mod.common.api.net.Decodable
import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.api.riding.conditions.RidingCondition
import com.cobblemon.mod.common.api.riding.properties.mounting.MountProperties
import com.cobblemon.mod.common.api.riding.properties.riding.RidingProperties
import com.cobblemon.mod.common.api.riding.properties.mounting.MountType
import com.cobblemon.mod.common.api.riding.seats.properties.SeatProperties
import com.google.gson.annotations.SerializedName
import net.minecraft.network.PacketByteBuf

data class CobblemonRidingProperties(
    @SerializedName("seats")
    private val _seats: List<SeatProperties>? = null,

    @SerializedName("conditions")
    private val _conditions: List<RidingCondition>? = null,

    @SerializedName("properties")
    private val _properties: Map<MountType, MountProperties>
): RidingProperties, Encodable, Decodable {

    companion object {
        fun unsupported() : CobblemonRidingProperties = CobblemonRidingProperties(_properties = emptyMap())
    }

    override fun supported(): Boolean {
        return this._properties.isNotEmpty() && this._seats?.isNotEmpty() ?: false
    }

    override fun seats(): List<SeatProperties> {
        return this._seats ?: listOf()
    }

    override fun conditions(): List<RidingCondition> {
        return this._conditions ?: listOf()
    }

    override fun properties(type: MountType): MountProperties? {
        return this._properties[type]
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeNullable(this._seats) { _, seats -> buffer.writeCollection(seats) { _, seat -> seat.encode(buffer) } }
//        for(entry in this._properties.entries) {
//            buffer.writeIdentifier(entry.key.identifier)
//
//        }
    }

    override fun decode(buffer: PacketByteBuf) {
        buffer.readNullable { _ -> buffer.readList { _ -> SeatProperties.decode(buffer) } }
    }
}