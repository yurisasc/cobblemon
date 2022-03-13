package com.cablemc.pokemoncobbled.common.api.battles.model.actor

import com.cablemc.pokemoncobbled.common.api.battles.model.ai.BattleAI
import com.cablemc.pokemoncobbled.common.api.storage.party.PartyStore
import com.cablemc.pokemoncobbled.common.battles.ActiveBattlePokemon
import com.cablemc.pokemoncobbled.common.battles.pokemon.BattlePokemon
import java.util.*
import java.util.concurrent.CompletableFuture

abstract class AIBattleActor(
    gameId: UUID,
    pokemonList: List<BattlePokemon>,
    val battleAI: BattleAI
) : BattleActor(gameId, pokemonList) {
    override fun getChoices(activePokemon: Iterable<ActiveBattlePokemon>): CompletableFuture<Iterable<String>> {
        return CompletableFuture.completedFuture(battleAI.chooseMoves(activePokemon))
    }

    override fun getSwitch(activePokemon: Iterable<ActiveBattlePokemon>): CompletableFuture<Iterable<UUID>> {
        return CompletableFuture.completedFuture(battleAI.chooseSwitches(activePokemon))
    }
}