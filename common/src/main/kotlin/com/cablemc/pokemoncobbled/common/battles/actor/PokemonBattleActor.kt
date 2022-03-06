package com.cablemc.pokemoncobbled.common.battles.actor

import com.cablemc.pokemoncobbled.common.api.battles.model.ai.BattleAI
import com.cablemc.pokemoncobbled.common.api.battles.model.actor.AIBattleActor
import com.cablemc.pokemoncobbled.common.api.storage.party.PartyStore
import com.cablemc.pokemoncobbled.common.battles.pokemon.BattlePokemon
import com.cablemc.pokemoncobbled.common.util.asTranslated
import java.util.UUID

class PokemonBattleActor(
    showdownId: String,
    gameId: UUID,
    val pokemon: BattlePokemon,
    artificialDecider: BattleAI
) : AIBattleActor(showdownId, gameId, listOf(pokemon, BattlePokemon(pokemon.effectedPokemon.clone())), artificialDecider) {
    override fun getName() = pokemon.effectedPokemon.species.translatedName.asTranslated() // TODO translate
}