package com.cablemc.pokemoncobbled.forge.common.net.messages.client.pokemon.update

import net.minecraft.network.FriendlyByteBuf

/**
 * A specific type of update for a Pokémon which updates a single string value
 *
 * @author Deltric
 * @since January 13th, 2022
 */
abstract class StringUpdatePacket : SingleUpdatePacket<String>("") {
    override fun encodeValue(buffer: FriendlyByteBuf, value: String) {
        buffer.writeUtf(value)
    }

    override fun decodeValue(buffer: FriendlyByteBuf): String {
        return buffer.readUtf()
    }
}