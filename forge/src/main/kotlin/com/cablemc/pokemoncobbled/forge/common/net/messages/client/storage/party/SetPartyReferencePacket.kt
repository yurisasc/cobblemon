package com.cablemc.pokemoncobbled.forge.common.net.messages.client.storage.party

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.forge.common.api.storage.PokemonStore
import net.minecraft.network.FriendlyByteBuf
import java.util.UUID

/**
 * Sets the given party store as the player's active party. This will change what the overlay
 * is showing, but will fail if the store supplied in this packet is one unknown to the client.
 * To inform the player about a store before doing this, [PokemonStore.sendTo] will serve this
 * purpose. If you have previously used [InitializePartyPacket] then that won't be necessary.
 *
 * Handled by [com.cablemc.pokemoncobbled.client.net.storage.party.SetPartyReferenceHandler]
 *
 * @author Hiroku
 * @since November 29th, 2021
 */
class SetPartyReferencePacket() : NetworkPacket {
    lateinit var storeID: UUID

    constructor(storageID: UUID): this() {
        this.storeID = storageID
    }

    override fun encode(buffer: FriendlyByteBuf) {
        buffer.writeUUID(this.storeID)
    }

    override fun decode(buffer: FriendlyByteBuf) {
        this.storeID = buffer.readUUID()
    }
}