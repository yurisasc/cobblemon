/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.client.battle

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.battles.BattleFormat
import com.cablemc.pokemoncobbled.common.net.messages.server.battle.BattleSelectActionsPacket
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
    val messages = ClientBattleMessageQueue()
    var mustChoose = false

    fun getFirstUnansweredRequest() = pendingActionRequests.firstOrNull { it.response == null }
    fun checkForFinishedChoosing() {
        if (getFirstUnansweredRequest() == null) {
            CobbledNetwork.sendToServer(
                BattleSelectActionsPacket(
                    battleId = battleId,
                    pendingActionRequests.map { it.response!! }
                )
            )
            mustChoose = false
        }
    }

    fun getPokemonFromPNX(pnx: String): Pair<ClientBattleActor, ActiveClientBattlePokemon> {
        val actor = sides.flatMap { it.actors }.find { it.showdownId == pnx.substring(0, 2) }
            ?: throw IllegalStateException("Invalid pnx: $pnx - unknown actor")
        val letter = pnx[2]
        val pokemon = actor.side.activeClientBattlePokemon.find { it.getLetter() == letter }
            ?: throw IllegalStateException("Invalid pnx: $pnx - unknown pokemon")
        return actor to pokemon
    }
}