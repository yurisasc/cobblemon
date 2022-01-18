package com.cablemc.pokemoncobbled.common.battles.subject

import com.cablemc.pokemoncobbled.common.api.battles.model.ai.ArtificialDecider
import com.cablemc.pokemoncobbled.common.api.battles.model.subject.AIBattleSubject
import com.cablemc.pokemoncobbled.common.api.storage.party.PartyStore
import java.util.UUID

class PokemonSubject(
    showdownId: String,
    gameId: UUID,
    party: PartyStore,
    artificialDecider: ArtificialDecider
) : AIBattleSubject(showdownId, gameId, party, artificialDecider) {



}