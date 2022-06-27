package com.cablemc.pokemoncobbled.common.api.pokemon.evolution.adapters

import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.Evolution
import com.google.gson.JsonDeserializer
import com.google.gson.JsonSerializer
import kotlin.reflect.KClass
import com.cablemc.pokemoncobbled.common.pokemon.evolution.adapters.CobbledEvolutionAdapter

/**
 * Saves and loads [Evolution]s with JSON.
 * For the default implementation see [CobbledEvolutionAdapter].
 *
 * @author Licious
 * @since March 20th, 2022
 */
interface EvolutionAdapter : JsonDeserializer<Evolution>, JsonSerializer<Evolution> {

    /**
     * Registers the given type of [Evolution] to it's associated ID for deserialization.
     *
     * @param T The type of [Evolution].
     * @param id The id of the evolution event.
     * @param type The [KClass] of the type.
     */
    fun <T : Evolution> registerType(id: String, type: KClass<T>)

}