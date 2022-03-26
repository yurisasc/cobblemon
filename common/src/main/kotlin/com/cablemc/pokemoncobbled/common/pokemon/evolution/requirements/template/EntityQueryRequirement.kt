package com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements.template

import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import net.minecraft.world.entity.LivingEntity

/**
 * An [EvolutionRequirement] that expects a [LivingEntity] to be attached to the [Pokemon].
 * It can be the [PokemonEntity] if present or the [LivingEntity] that owns it.
 *
 * Whenever an entity can't be resolved [EvolutionRequirement.check] will never succeed.
 *
 * @author Licious
 * @since March 21st, 2022
 */
abstract class EntityQueryRequirement : EvolutionRequirement {

    final override fun check(pokemon: Pokemon): Boolean {
        val queriedEntity = pokemon.entity ?: pokemon.getOwnerPlayer() ?: return false
        return this.check(pokemon, queriedEntity)
    }

    /**
     * Checks if the given [Pokemon] & [LivingEntity] satisfies the requirement.
     *
     * @param pokemon The [Pokemon] being queried.
     * @param queriedEntity The [LivingEntity] that can be compared in context of a [Level].
     * @return If the requirement was satisfied.
     */
    abstract fun check(pokemon: Pokemon, queriedEntity: LivingEntity): Boolean

}