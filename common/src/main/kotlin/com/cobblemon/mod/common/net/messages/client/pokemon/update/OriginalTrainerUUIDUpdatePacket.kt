package com.cobblemon.mod.common.net.messages.client.pokemon.update

import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.network.PacketByteBuf
import java.util.*

class OriginalTrainerUUIDUpdatePacket(pokemon: () -> Pokemon, tetheringId: UUID?) : SingleUpdatePacket<UUID?, OriginalTrainerUUIDUpdatePacket>(pokemon, tetheringId) {
    override val id = ID

    override fun encodeValue(buffer: PacketByteBuf) {
        buffer.writeNullable(this.value) { _, v -> buffer.writeUuid(v) }
    }

    override fun set(pokemon: Pokemon, value: UUID?) {
        if (value != null)
            pokemon.setOriginalTrainer(value, pokemon.originalTrainerDisplayName)
        else
            pokemon.setOriginalTrainer(pokemon.originalTrainerDisplayName)
    }

    companion object {
        val ID = cobblemonResource("original_trainer_uuid_update")
        fun decode(buffer: PacketByteBuf): OriginalTrainerUUIDUpdatePacket {
            val pokemon = decodePokemon(buffer)
            val originalTrainerUUID = buffer.readNullable { buffer.readUuid() }
            return OriginalTrainerUUIDUpdatePacket(pokemon, originalTrainerUUID)
        }
    }
}