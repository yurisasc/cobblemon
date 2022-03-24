package com.cablemc.pokemoncobbled.common.api.pokemon.evolution

import com.cablemc.pokemoncobbled.common.api.moves.MoveTemplate
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.evolution.LevelEvolution
import com.cablemc.pokemoncobbled.common.pokemon.evolution.TradeEvolution
import com.cablemc.pokemoncobbled.common.pokemon.evolution.ItemInteractionEvolution

/**
 * Represents an evolution of a [Pokemon].
 * Following Pokemon these can be triggered by 3 possible events, level ups, trades or using an item.
 * For the default implementations see [LevelEvolution], [TradeEvolution] & [ItemInteractionEvolution].
 * Also see [PassiveEvolution] & [ContextEvolution].
 *
 * @author Licious
 * @since March 19th, 2022
 */
interface Evolution {

    /**
     * The unique id of the evolution.
     * It should be human readable, I.E pikachu_level
     */
    val id: String

    /**
     * The result of this evolution.
     */
    val result: PokemonProperties

    /**
     * If this evolution will allow the user to choose when to start it or not.
     */
    var optional: Boolean

    // ToDo pending held items.
    /**
     * If this [Evolution] will consume the [Pokemon.heldItem]
     */
    var consumeHeldItem: Boolean

    /**
     * The [EvolutionRequirement]s behind this evolution.
     */
    val requirements: MutableSet<EvolutionRequirement>

    /**
     * The [MoveTemplate]s that will be offered to be learnt upon evolving.
     */
    val learnableMoves: MutableSet<MoveTemplate>

    /**
     * Checks if the given [Pokemon] passes all the conditions and is ready to evolve.
     *
     * @param pokemon The [Pokemon] being queried.
     * @return If the [Evolution] can start.
     */
    fun test(pokemon: Pokemon) = this.requirements.all { requirement -> requirement.check(pokemon) }

    /**
     * Starts this evolution or queues it if [optional] is true.
     * Side effects may occur based on [consumeHeldItem].
     *
     * @param pokemon The [Pokemon] being evolved.
     */
    fun evolve(pokemon: Pokemon) {
        this.result.apply(pokemon)
        // ToDo actually queue the client if needed or start the proper animation
    }

}