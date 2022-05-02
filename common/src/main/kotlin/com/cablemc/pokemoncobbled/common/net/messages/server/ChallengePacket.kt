package com.cablemc.pokemoncobbled.common.net.messages.server

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import net.minecraft.network.PacketByteBuf
import java.util.UUID

class ChallengePacket() : NetworkPacket {
    var targetedEntityId: Int = -1
    lateinit var selectedPokemonId: UUID

    constructor(targetedEntityId: Int, selectedPokemonId: UUID): this() {
        this.targetedEntityId = targetedEntityId
        this.selectedPokemonId = selectedPokemonId
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeInt(this.targetedEntityId)
        buffer.writeUuid(this.selectedPokemonId)
    }

    override fun decode(buffer: PacketByteBuf) {
        this.targetedEntityId = buffer.readInt()
        this.selectedPokemonId = buffer.readUuid()
    }
}