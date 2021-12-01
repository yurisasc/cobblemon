package com.cablemc.pokemoncobbled.common.net.messages.client.storage

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.api.storage.StorePosition
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import io.netty.buffer.ByteBuf
import net.minecraft.network.FriendlyByteBuf
import java.util.UUID

/**
 * Base packet class for creating a new Pokémon in one of the client's stores. This
 * is for when the Pokémon is currently unrecognized by that specific store on the
 * client. Implementations target specific store types.
 *
 * @author Hiroku
 * @since November 29th, 2021
 */
abstract class SetPokemonPacket<T : StorePosition> : NetworkPacket {
    var pokemon = Pokemon()
    var storeID = UUID.randomUUID()
    lateinit var storePosition: T

    abstract fun encodePosition(buffer: FriendlyByteBuf): ByteBuf
    override fun encode(buffer: FriendlyByteBuf) {
        pokemon.saveToBuffer(buffer)
        buffer.writeUUID(storeID)
        encodePosition(buffer)
    }

    abstract fun decodePosition(buffer: FriendlyByteBuf): T
    override fun decode(buffer: FriendlyByteBuf) {
        pokemon.loadFromBuffer(buffer)
        storeID = buffer.readUUID()
        storePosition = decodePosition(buffer)
    }
}