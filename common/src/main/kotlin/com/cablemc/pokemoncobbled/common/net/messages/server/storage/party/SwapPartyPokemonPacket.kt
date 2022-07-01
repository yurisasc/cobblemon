package com.cablemc.pokemoncobbled.common.net.messages.server.storage.party

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.api.storage.party.PartyPosition
import com.cablemc.pokemoncobbled.common.api.storage.party.PartyPosition.Companion.readPartyPosition
import com.cablemc.pokemoncobbled.common.api.storage.party.PartyPosition.Companion.writePartyPosition
import com.cablemc.pokemoncobbled.common.net.serverhandling.storage.party.SwapPartyPokemonHandler
import net.minecraft.network.PacketByteBuf
import java.util.UUID

/**
 * Tells the server to swap two Pokémon in the player's party.
 *
 * Handled by [SwapPartyPokemonHandler].
 *
 * @author Hiroku
 * @since June 20th, 2022
 */
class SwapPartyPokemonPacket() : NetworkPacket {
    lateinit var pokemon1ID: UUID
    lateinit var position1: PartyPosition
    lateinit var pokemon2ID: UUID
    lateinit var position2: PartyPosition

    constructor(pokemon1ID: UUID, position1: PartyPosition, pokemon2ID: UUID, position2: PartyPosition): this() {
        this.pokemon1ID = pokemon1ID
        this.position1 = position1
        this.pokemon2ID = pokemon2ID
        this.position2 = position2
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeUuid(pokemon1ID)
        buffer.writePartyPosition(position1)
        buffer.writeUuid(pokemon2ID)
        buffer.writePartyPosition(position2)
    }

    override fun decode(buffer: PacketByteBuf) {
        pokemon1ID = buffer.readUuid()
        position1 = buffer.readPartyPosition()
        pokemon2ID = buffer.readUuid()
        position2 = buffer.readPartyPosition()
    }
}