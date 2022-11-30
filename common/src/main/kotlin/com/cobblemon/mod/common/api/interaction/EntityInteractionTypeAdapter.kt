package com.cobblemon.mod.common.api.interaction

import com.cobblemon.mod.common.util.adapters.CobblemonPokemonEntityInteractionTypeAdapter
import com.google.gson.JsonDeserializer
import com.google.gson.JsonSerializer
import net.minecraft.util.Identifier
import kotlin.reflect.KClass

/**
 * A type adapter for [EntityInteraction]s.
 * For the default implementation with Pokémon entities see [CobblemonPokemonEntityInteractionTypeAdapter].
 *
 * @param T The type of [EntityInteraction].
 *
 * @author Licious
 * @since November 30th, 2022
 */
interface EntityInteractionTypeAdapter<T : EntityInteraction<*>> : JsonDeserializer<T>, JsonSerializer<T> {

    fun registerInteraction(identifier: Identifier, type: KClass<out T>)

}