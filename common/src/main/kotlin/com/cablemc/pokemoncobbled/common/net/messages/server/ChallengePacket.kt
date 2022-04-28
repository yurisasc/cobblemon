package com.cablemc.pokemoncobbled.common.net.messages.server

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import net.minecraft.network.FriendlyByteBuf
import java.util.UUID

class ChallengePacket() : NetworkPacket {
    var targetedEntityId: Int = -1
    lateinit var selectedPokemonId: UUID

    constructor(targetedEntityId: Int, selectedPokemonId: UUID): this() {
        this.targetedEntityId = targetedEntityId
        this.selectedPokemonId = selectedPokemonId
    }

    override fun encode(buffer: FriendlyByteBuf) {
        buffer.writeInt(this.targetedEntityId)
        buffer.writeUUID(this.selectedPokemonId)
    }

    override fun decode(buffer: FriendlyByteBuf) {
        this.targetedEntityId = buffer.readInt()
        this.selectedPokemonId = buffer.readUUID()
    }
}