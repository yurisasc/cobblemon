package com.cablemc.pokemoncobbled.common.api.battles.model.ai

import com.cablemc.pokemoncobbled.common.api.battles.model.PokemonBattle
import com.cablemc.pokemoncobbled.common.api.battles.model.actor.AIBattleActor
import com.cablemc.pokemoncobbled.common.api.battles.model.actor.BattleActor
import com.cablemc.pokemoncobbled.common.api.moves.Move
import com.cablemc.pokemoncobbled.common.battles.ActiveBattlePokemon
import java.util.UUID
import java.util.concurrent.CompletableFuture

/**
 * Interface for an actors battle AI
 *
 * @since January 16th, 2022
 * @author Deltric
 */
interface BattleAI {
    /**
     * Requests that the AI choose moves for the given Pokémon
     * @return the move choice
     */
    fun chooseMoves(activePokemon: Iterable<ActiveBattlePokemon>) : Iterable<String>
    fun chooseSwitches(activePokemon: Iterable<ActiveBattlePokemon>): Iterable<UUID>
}