package com.cablemc.pokemoncobbled.common.api.abilities

import com.cablemc.pokemoncobbled.common.PokemonCobbled.LOGGER
import com.cablemc.pokemoncobbled.common.api.PrioritizedList
import com.cablemc.pokemoncobbled.common.api.Priority
import com.cablemc.pokemoncobbled.common.pokemon.Species

/**
 * A pool of potential abilities, as a [PrioritizedList]. The added logic of this subclass
 * is that it has selection logic. Given a species and a set of aspects, it will go through
 * each priority group, and if it can then it will pick from that group and return it.
 *
 * @author Hiroku
 * @since July 28th, 2022
 */
open class AbilityPool : PrioritizedList<PotentialAbility>() {
    fun select(species: Species, aspects: Set<String>): Ability {
        for (priority in Priority.values()) {
            val potentialAbilities = priorityMap[priority]?.filter { it.isSatisfiedBy(aspects) } ?: continue
            if (potentialAbilities.isNotEmpty()) {
                return potentialAbilities.random().template.create()
            }
        }

        LOGGER.error("Unable to select an ability from the pool for $species and aspects: ${aspects.joinToString()}")
        return Abilities.RUN_AWAY.create()
    }
}