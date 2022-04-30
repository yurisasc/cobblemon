package com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update

import net.minecraft.network.PacketByteBuf

/**
 * A specific type of update for a Pokémon which updates a single string value
 *
 * @author Deltric
 * @since January 13th, 2022
 */
abstract class StringUpdatePacket : SingleUpdatePacket<String>("") {
    override fun encodeValue(buffer: PacketByteBuf, value: String) {
        buffer.writeString(value)
    }

    override fun decodeValue(buffer: PacketByteBuf): String {
        return buffer.readString()
    }
}