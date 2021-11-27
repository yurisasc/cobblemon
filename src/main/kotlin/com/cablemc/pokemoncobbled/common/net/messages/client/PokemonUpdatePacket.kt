package com.cablemc.pokemoncobbled.common.net.messages.client

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import net.minecraft.network.FriendlyByteBuf

/**
 * Handler: [com.cablemc.pokemoncobbled.client.net.PokemonUpdateHandler]
 */
class PokemonUpdatePacket(): NetworkPacket {
    var id: String = ""

    constructor(id: String): this() {
        this.id = id
    }

    override fun encode(buffer: FriendlyByteBuf) {
        buffer.writeUtf(id)
    }

    override fun decode(buffer: FriendlyByteBuf) {
        id = buffer.readUtf()
    }
}