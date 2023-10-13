/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.client.pokemon.update

import com.cobblemon.mod.common.api.pokemon.gender.Gender
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.network.PacketByteBuf

class GenderUpdatePacket(pokemon: () -> Pokemon, value: Gender): SingleUpdatePacket<Gender, GenderUpdatePacket>(pokemon, value) {
    override val id = ID
    override fun encodeValue(buffer: PacketByteBuf) {
        buffer.writeEnumConstant(this.value)
    }

    override fun set(pokemon: Pokemon, value: Gender) {
        pokemon.gender = value
    }

    companion object {
        val ID = cobblemonResource("gender_update")
        fun decode(buffer: PacketByteBuf): GenderUpdatePacket {
            val pokemon = decodePokemon(buffer)
            val gender = buffer.readEnumConstant(Gender::class.java)
            return GenderUpdatePacket(pokemon, gender)
        }
    }
}