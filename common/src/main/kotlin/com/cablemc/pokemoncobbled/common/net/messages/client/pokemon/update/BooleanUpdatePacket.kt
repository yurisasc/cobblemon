package com.cablemc.pokemoncobbled.forge.common.net.messages.client.pokemon.update

import net.minecraft.network.FriendlyByteBuf

/**
 * A specific type of update for a Pokémon which updates a single boolean value
 *
 * @author Deltric
 * @since January 13th, 2022
 */
abstract class BooleanUpdatePacket : SingleUpdatePacket<Boolean>(false) {
    override fun encodeValue(buffer: FriendlyByteBuf, value: Boolean) {
        buffer.writeBoolean(value)
    }

    override fun decodeValue(buffer: FriendlyByteBuf): Boolean {
        return buffer.readBoolean()
    }
}