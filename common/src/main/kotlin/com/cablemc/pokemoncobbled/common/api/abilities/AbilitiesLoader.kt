package com.cablemc.pokemoncobbled.common.api.abilities

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.util.fromJson
import com.google.gson.GsonBuilder
import java.io.InputStreamReader

object AbilitiesLoader {
    val GSON = GsonBuilder()
        .disableHtmlEscaping()
        .create()

    fun loadFromAssets(name: String): AbilityTemplate {
        val inputStream = javaClass.getResourceAsStream("/assets/${PokemonCobbled.MODID}/abilities/$name.json")!!
        val abilityTemplate = GSON.fromJson<AbilityTemplate>(InputStreamReader(inputStream))
        abilityTemplate.createTextComponents()
        return abilityTemplate
    }
}