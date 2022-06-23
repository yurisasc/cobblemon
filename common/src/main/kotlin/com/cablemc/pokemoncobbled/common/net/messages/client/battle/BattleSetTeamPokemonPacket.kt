package com.cablemc.pokemoncobbled.common.net.messages.client.battle

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.net.IntSize
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.readSizedInt
import com.cablemc.pokemoncobbled.common.util.writeSizedInt
import net.minecraft.network.PacketByteBuf


/**
 * Gives the client the true details of their team in the battle. This is so that switch choices can be made with
 * full details.
 *
 * Handled by [com.cablemc.pokemoncobbled.common.client.net.battle.BattleSetTeamPokemonHandler].
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