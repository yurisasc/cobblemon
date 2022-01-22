package com.cablemc.pokemoncobbled.common.battles.actor

import com.cablemc.pokemoncobbled.common.api.battles.model.ai.BattleAI
import com.cablemc.pokemoncobbled.common.api.battles.model.actor.AIBattleActor
import com.cablemc.pokemoncobbled.common.api.storage.party.PartyStore
import java.util.UUID

class TrainerBattleActor(
    showdownId: String,
    gameId: UUID,
    party: PartyStore,
    artificialDecider: BattleAI
) : AIBattleActor(showdownId, gameId, party, artificialDecider) {



}