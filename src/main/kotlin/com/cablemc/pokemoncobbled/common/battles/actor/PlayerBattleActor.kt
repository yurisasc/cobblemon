package com.cablemc.pokemoncobbled.common.battles.actor

import com.cablemc.pokemoncobbled.common.api.battles.model.actor.BattleActor
import com.cablemc.pokemoncobbled.common.api.storage.party.PartyStore
import java.util.UUID

class PlayerBattleActor(
    showdownId: String,
    gameId: UUID,
    party: PartyStore
) : BattleActor(showdownId, gameId, party) {



}