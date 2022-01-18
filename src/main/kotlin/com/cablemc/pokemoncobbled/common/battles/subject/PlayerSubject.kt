package com.cablemc.pokemoncobbled.common.battles.subject

import com.cablemc.pokemoncobbled.common.api.battles.model.subject.BattleSubject
import com.cablemc.pokemoncobbled.common.api.storage.party.PartyStore
import java.util.UUID

class PlayerSubject(
    showdownId: String,
    gameId: UUID,
    party: PartyStore
) : BattleSubject(showdownId, gameId, party) {



}