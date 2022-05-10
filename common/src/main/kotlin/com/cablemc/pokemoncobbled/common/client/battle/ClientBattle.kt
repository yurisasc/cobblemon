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
}