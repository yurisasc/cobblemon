/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.net.messages.client.battle

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.network.PacketByteBuf

/**
 * Packet sent to the client to give a player a total update of one of their battle
 * Pokémon's data. Unlike other update packets this gives complete and private data,
 * unaffected by the 'fog of war' in battles (knowing all the moves, for example).
 *
 * Handled by [com.cablemc.pokemoncobbled.common.client.net.battle.BattleUpdateTeamPokemonHandler].
 *
 * @author Hiroku
 * @since August 27th, 2022
 */
class BattleUpdateTeamPokemonPacket internal constructor() : NetworkPacket {
    lateinit var pokemon: Pokemon

    constructor(pokemon: Pokemon): this() {
        this.pokemon = pokemon
    }

    override fun encode(buffer: PacketByteBuf) {
        pokemon.saveToBuffer(buffer, toClient = true)
    }

    override fun decode(buffer: PacketByteBuf) {
        pokemon = Pokemon().loadFromBuffer(buffer)
    }
}