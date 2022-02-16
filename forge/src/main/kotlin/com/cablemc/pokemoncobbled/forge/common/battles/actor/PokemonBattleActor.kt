package com.cablemc.pokemoncobbled.forge.common.battles.actor

import com.cablemc.pokemoncobbled.forge.common.api.battles.model.ai.BattleAI
import com.cablemc.pokemoncobbled.forge.common.api.battles.model.actor.AIBattleActor
import com.cablemc.pokemoncobbled.forge.common.api.storage.party.PartyStore
import java.util.UUID

class PokemonBattleActor(
    showdownId: String,
    gameId: UUID,
    party: PartyStore,
    artificialDecider: BattleAI
) : AIBattleActor(showdownId, gameId, party, artificialDecider) {

}