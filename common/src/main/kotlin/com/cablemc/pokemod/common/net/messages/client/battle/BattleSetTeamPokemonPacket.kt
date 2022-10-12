/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.net.messages.client.battle

import com.cablemc.pokemod.common.api.net.NetworkPacket
import com.cablemc.pokemod.common.net.IntSize
import com.cablemc.pokemod.common.pokemon.Pokemon
import com.cablemc.pokemod.common.util.readSizedInt
import com.cablemc.pokemod.common.util.writeSizedInt
import net.minecraft.network.PacketByteBuf


/**
 * Gives the client the true details of their team in the battle. This is so that switch choices can be made with
 * full details.
 *
 * Handled by [com.cablemc.pokemod.common.client.net.battle.BattleSetTeamPokemonHandler].
 *
 * @author Hiroku
 * @since June 6th, 2022
 */
class BattleSetTeamPokemonPacket() : NetworkPacket {
    val team = mutableListOf<Pokemon>()

    constructor(team: List<Pokemon>): this() {
        this.team.addAll(team)
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeSizedInt(IntSize.U_BYTE, team.size)
        for (pokemon in team) {
            pokemon.saveToBuffer(buffer, toClient = true)
        }
    }

    override fun decode(buffer: PacketByteBuf) {
        repeat(times = buffer.readSizedInt(IntSize.U_BYTE)) {
            team.add(Pokemon().loadFromBuffer(buffer))
        }
    }
}