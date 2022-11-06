package com.cobblemon.mod.common.api.pokemon.stats

import com.google.gson.JsonDeserializer
import com.google.gson.JsonSerializer

/**
 * A type adapter responsible for (de)serializing [Stat]s.
 *
 * @author Licious
 * @since November 6th, 2022
 */
interface StatTypeAdapter : JsonDeserializer<Stat>, JsonSerializer<Stat>