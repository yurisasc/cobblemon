package com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update

import net.minecraft.network.PacketByteBuf

/**
 * A specific type of update for a Pokémon which updates a single boolean value
 *
 * @author Deltric
 * @since January 13th, 2022
 */
abstract class BooleanUpdatePacket : SingleUpdatePacket<Boolean>(false) {
    override fun encodeValue(buffer: PacketByteBuf, value: Boolean) {
        buffer.writeBoolean(value)
    }

    override fun decodeValue(buffer: PacketByteBuf): Boolean {
        return buffer.readBoolean()
    }
}