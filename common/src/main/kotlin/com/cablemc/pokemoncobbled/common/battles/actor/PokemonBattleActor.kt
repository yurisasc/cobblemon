package com.cablemc.pokemoncobbled.common.battles.actor

import com.cablemc.pokemoncobbled.common.api.battles.model.actor.AIBattleActor
import com.cablemc.pokemoncobbled.common.api.battles.model.actor.ActorType
import com.cablemc.pokemoncobbled.common.api.battles.model.ai.BattleAI
import com.cablemc.pokemoncobbled.common.battles.ai.RandomBattleAI
import com.cablemc.pokemoncobbled.common.battles.pokemon.BattlePokemon
import java.util.UUID

open class PokemonBattleActor(
    uuid: UUID,
    val pokemon: BattlePokemon,
    artificialDecider: BattleAI = RandomBattleAI()
) : AIBattleActor(uuid, listOf(pokemon), artificialDecider) {
    override fun getName() = pokemon.effectedPokemon.species.translatedName
    override val type = ActorType.WILD
}