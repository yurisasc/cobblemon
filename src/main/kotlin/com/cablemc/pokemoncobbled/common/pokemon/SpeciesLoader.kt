package com.cablemc.pokemoncobbled.common.pokemon

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.abilities.AbilityTemplate
import com.cablemc.pokemoncobbled.common.api.abilities.adapters.AbilityTemplateAdapter
import com.cablemc.pokemoncobbled.common.api.types.ElementalType
import com.cablemc.pokemoncobbled.common.api.types.adapters.ElementalTypeAdapter
import com.cablemc.pokemoncobbled.common.pokemon.adapters.StatAdapter
import com.cablemc.pokemoncobbled.common.pokemon.stats.Stat
import com.cablemc.pokemoncobbled.common.util.fromJson
import com.google.gson.GsonBuilder
import java.io.InputStreamReader

object SpeciesLoader {
    val GSON = GsonBuilder()
        .registerTypeAdapter(Stat::class.java, StatAdapter)
        .registerTypeAdapter(ElementalType::class.java, ElementalTypeAdapter)
        .registerTypeAdapter(AbilityTemplate::class.java, AbilityTemplateAdapter)
        .disableHtmlEscaping()
        .create()

    fun loadFromAssets(name: String): Species {
        // TODO add proper error handling
        val inputStream = javaClass.getResourceAsStream("/assets/${PokemonCobbled.MODID}/species/$name.json")!!
        return GSON.fromJson<Species>(InputStreamReader(inputStream))
    }
}