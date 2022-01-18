package com.cablemc.pokemoncobbled.common.api.battles.model.subject

import com.cablemc.pokemoncobbled.common.api.battles.model.ai.ArtificialDecider
import com.cablemc.pokemoncobbled.common.api.storage.party.PartyStore
import java.util.*

open class AIBattleSubject(
    showdownId: String,
    gameId: UUID,
    party: PartyStore,
    val artificialDecider: ArtificialDecider
) : BattleSubject(showdownId, gameId, party) {

}