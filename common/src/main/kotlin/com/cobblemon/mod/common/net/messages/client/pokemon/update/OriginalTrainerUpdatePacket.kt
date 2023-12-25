/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.client.pokemon.update

import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.network.PacketByteBuf
import java.util.*

class OriginalTrainerUpdatePacket(pokemon: () -> Pokemon, uuidAndUsername: Pair<UUID?, String>?) : SingleUpdatePacket<Pair<UUID?, String>?, OriginalTrainerUpdatePacket>(pokemon, uuidAndUsername) {
    override val id = ID

    override fun encodeValue(buffer: PacketByteBuf) {
        buffer.writeNullable(this.value) { _, v ->
            buffer.writeNullable(v.first) { _, uuid -> buffer.writeUuid(uuid) }
            buffer.writeString(v.second)
        }
    }

    override fun set(pokemon: Pokemon, value: Pair<UUID?, String>?) {
        if (value != null) {
            pokemon.setOriginalTrainer(value.first, value.second)
        } else {
            pokemon.removeOriginalTrainer()
        }
    }

    companion object {
        val ID = cobblemonResource("original_trainer_update")
        fun decode(buffer: PacketByteBuf): OriginalTrainerUpdatePacket {
            val pokemon = decodePokemon(buffer)
            val originalTrainer = buffer.readNullable {
                val uuid = buffer.readNullable { buffer.readUuid() }
                val username = buffer.readString()
                uuid to username
            }
            return OriginalTrainerUpdatePacket(pokemon, originalTrainer)
        }
    }
}