package com.cablemc.pokemoncobbled.common.api.battles.model.subject

import com.cablemc.pokemoncobbled.common.api.storage.party.PartyStore
import java.util.*

open class BattleSubject(
    val showdownId: String,
    val gameId: UUID,
    val party: PartyStore
) {

}