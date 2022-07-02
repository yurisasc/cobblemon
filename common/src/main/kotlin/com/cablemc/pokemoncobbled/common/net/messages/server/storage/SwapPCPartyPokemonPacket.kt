package com.cablemc.pokemoncobbled.common.net.messages.server.storage

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.api.storage.party.PartyPosition
import com.cablemc.pokemoncobbled.common.api.storage.party.PartyPosition.Companion.readPartyPosition
import com.cablemc.pokemoncobbled.common.api.storage.party.PartyPosition.Companion.writePartyPosition
import com.cablemc.pokemoncobbled.common.api.storage.pc.PCPosition
import com.cablemc.pokemoncobbled.common.api.storage.pc.PCPosition.Companion.readPCPosition
import com.cablemc.pokemoncobbled.common.api.storage.pc.PCPosition.Companion.writePCPosition
import com.cablemc.pokemoncobbled.common.net.serverhandling.storage.SwapPCPartyPokemonHandler
import net.minecraft.network.PacketByteBuf
import java.util.UUID

/**
 * Tells the server to swap Pokémon between the party and the currently linked PC. The positions are sent
 * along with the IDs to validate that the client is making a synchronized request.
 *
 * Handled by [SwapPCPartyPokemonHandler].
 *
 * @author Hiroku
 * @since June 20th, 2022
 */
class SwapPCPartyPokemonPacket() : NetworkPacket {
    lateinit var partyPokemonID: UUID
    lateinit var partyPosition: PartyPosition
    lateinit var pcPokemonID: UUID
    lateinit var pcPosition: PCPosition

    constructor(partyPokemonID: UUID, partyPosition: PartyPosition, pcPokemonID: UUID, pcPosition: PCPosition) : this() {
        this.partyPokemonID = partyPokemonID
        this.partyPosition = partyPosition
        this.pcPokemonID = pcPokemonID
        this.pcPosition = pcPosition
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeUuid(partyPokemonID)
        buffer.writePartyPosition(partyPosition)
        buffer.writeUuid(pcPokemonID)
        buffer.writePCPosition(pcPosition)
    }

    override fun decode(buffer: PacketByteBuf) {
        partyPokemonID = buffer.readUuid()
        partyPosition = buffer.readPartyPosition()
        pcPokemonID = buffer.readUuid()
        pcPosition = buffer.readPCPosition()
    }
}