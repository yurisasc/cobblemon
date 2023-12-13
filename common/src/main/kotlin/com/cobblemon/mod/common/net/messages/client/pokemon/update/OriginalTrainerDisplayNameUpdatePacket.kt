package com.cobblemon.mod.common.net.messages.client.pokemon.update

import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.network.PacketByteBuf

class OriginalTrainerDisplayNameUpdatePacket(pokemon: () -> Pokemon, displayName: String) : SingleUpdatePacket<String, OriginalTrainerDisplayNameUpdatePacket>(pokemon, displayName) {
    override val id = ID

    override fun encodeValue(buffer: PacketByteBuf) {
        buffer.writeNullable(this.value) { _, v -> buffer.writeString(v) }
    }

    override fun set(pokemon: Pokemon, value: String) {
        if (pokemon.originalTrainerUUID != null)
            pokemon.setOriginalTrainer(pokemon.originalTrainerUUID!!, value)
        else
            pokemon.setOriginalTrainer(value)
    }

    companion object {
        val ID = cobblemonResource("original_trainer_display_name_update")
        fun decode(buffer: PacketByteBuf): OriginalTrainerDisplayNameUpdatePacket {
            val pokemon = decodePokemon(buffer)
            val originalTrainerDisplayName = buffer.readString()
            return OriginalTrainerDisplayNameUpdatePacket(pokemon, originalTrainerDisplayName)
        }
    }
}