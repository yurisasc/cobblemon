package com.cablemc.pokemoncobbled.common.api.moves

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.util.fromJson
import com.google.gson.GsonBuilder
import java.io.InputStreamReader

object MoveLoader {
    val GSON = GsonBuilder()
        .disableHtmlEscaping()
        .create()

    fun loadFromAssets(name: String): MoveTemplate {
        val inputStream = javaClass.getResourceAsStream("/assets/${PokemonCobbled.MODID}/moves/$name.json")!!
        return GSON.fromJson<MoveTemplate>(InputStreamReader(inputStream))
    }
}