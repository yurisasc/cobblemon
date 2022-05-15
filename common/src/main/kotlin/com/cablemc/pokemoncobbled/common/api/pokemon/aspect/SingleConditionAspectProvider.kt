package com.cablemc.pokemoncobbled.common.api.pokemon.aspect

import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

/**
 * A specific type of [AspectProvider] which, upon satisfying some condition,
 * returns a single aspect. This is just a convenient interface for common usages.
 *
 * @author Hiroku
 * @since May 13th, 2022
 */
interface SingleConditionalAspectProvider : AspectProvider {
    /** The aspect to add if the conditions are met. */
    val aspect: String
    fun meetsCondition(pokemon: Pokemon): Boolean
    fun meetsCondition(pokemonProperties: PokemonProperties): Boolean

    override fun provide(properties: PokemonProperties): Set<String> {
        return if (meetsCondition(properties)) {
            setOf(aspect)
        } else {
            emptySet()
        }
    }

    override fun provide(pokemon: Pokemon): Set<String> {
        return if (meetsCondition(pokemon)) {
            setOf(aspect)
        } else {
            emptySet()
        }
    }
}