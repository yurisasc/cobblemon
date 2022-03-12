package com.cablemc.pokemoncobbled.common.api.battles.model.actor

import com.cablemc.pokemoncobbled.common.api.battles.model.ai.BattleAI
import com.cablemc.pokemoncobbled.common.api.storage.party.PartyStore
import com.cablemc.pokemoncobbled.common.battles.pokemon.BattlePokemon
import java.util.*

abstract class AIBattleActor(
    gameId: UUID,
    pokemonList: List<BattlePokemon>,
    val battleAI: BattleAI
) : BattleActor(gameId, pokemonList) {

}