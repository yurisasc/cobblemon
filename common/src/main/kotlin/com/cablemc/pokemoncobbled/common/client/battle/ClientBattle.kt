package com.cablemc.pokemoncobbled.common.client.battle

import com.cablemc.pokemoncobbled.common.battles.BattleFormat
import java.util.UUID

class ClientBattle(
    val battleId: UUID,
    val battleFormat: BattleFormat
) {
    var minimised = true
    var spectating = false

    val side1 = ClientBattleSide()
    val side2 = ClientBattleSide()

    val sides: Array<ClientBattleSide>
        get() = arrayOf(side1, side2)

    var pendingActionRequests = mutableListOf<SingleActionRequest>()
    var mustChoose = false

    fun getFirstUnansweredRequest() = pendingActionRequests.firstOrNull { it.response == null }

    fun getPokemonFromPNX(pnx: String): Pair<ClientBattleActor, ActiveClientBattlePokemon> {
        val actor = sides.flatMap { it.actors }.find { it.showdownId == pnx.substring(0, 2) }
            ?: throw IllegalStateException("Invalid pnx: $pnx - unknown actor")
        val letter = pnx[2]
        val pokemon = actor.side.activeClientBattlePokemon.find { it.getLetter() == letter }
            ?: throw IllegalStateException("Invalid pnx: $pnx - unknown pokemon")
        return actor to pokemon
    }
}