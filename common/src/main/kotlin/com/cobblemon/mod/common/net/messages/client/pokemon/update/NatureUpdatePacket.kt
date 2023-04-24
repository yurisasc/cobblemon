/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.client.pokemon.update

import com.cobblemon.mod.common.api.pokemon.Natures
import com.cobblemon.mod.common.net.messages.client.PokemonUpdatePacket
import com.cobblemon.mod.common.pokemon.Nature
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.network.PacketByteBuf

class NatureUpdatePacket(pokemon: Pokemon, val nature: Nature, val minted: Boolean) : PokemonUpdatePacket<NatureUpdatePacket>(pokemon) {

    override val id = ID

    override fun encodeDetails(buffer: PacketByteBuf) {
        buffer.writeIdentifier(this.nature.name)
        buffer.writeBoolean(this.minted)
    }

    override fun applyToPokemon() {
        if (this.minted) {
            this.pokemon.mintedNature = this.nature
        }
        else {
            this.pokemon.nature = this.nature
        }
    }

    companion object {
        val ID = cobblemonResource("nature_update")
        fun decode(buffer: PacketByteBuf) = NatureUpdatePacket(decodePokemon(buffer), Natures.getNature(buffer.readIdentifier())!!, buffer.readBoolean())

    }

}