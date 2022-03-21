package com.cablemc.pokemoncobbled.common.api.pokemon.evolution.adapters

import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.Evolution
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cablemc.pokemoncobbled.common.pokemon.evolution.adapters.CobbledRequirementAdapter
import com.google.gson.JsonDeserializer
import com.google.gson.JsonSerializer
import kotlin.reflect.KClass

/**
 * Saves and loads [EvolutionRequirement]s with JSON.
 * For the default implementation see [CobbledRequirementAdapter].
 *
 * @author Licious
 * @since March 21st, 2022
 */
interface RequirementAdapter : JsonDeserializer<EvolutionRequirement>, JsonSerializer<EvolutionRequirement> {

    /**
     * Registers the given type of [EvolutionRequirement] to it's associated ID for deserialization.
     *
     * @param T The type of [EvolutionRequirement].
     * @param id The id of the evolution event.
     * @param type The [KClass] of the type.
     */
    fun <T : EvolutionRequirement> registerType(id: String, type: KClass<T>)

}