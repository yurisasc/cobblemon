package com.cablemc.pokemoncobbled.common.api.moves

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.moves.adapters.DamageCategoryAdapter
import com.cablemc.pokemoncobbled.common.api.moves.categories.DamageCategory
import com.cablemc.pokemoncobbled.common.api.types.ElementalType
import com.cablemc.pokemoncobbled.common.api.types.adapters.ElementalTypeAdapter
import com.cablemc.pokemoncobbled.common.util.fromJson
import com.google.gson.GsonBuilder
import java.io.InputStreamReader

object MoveLoader {
    val GSON = GsonBuilder()
        .registerTypeAdapter(DamageCategory::class.java, DamageCategoryAdapter)
        .registerTypeAdapter(ElementalType::class.java, ElementalTypeAdapter)
        .disableHtmlEscaping()
        .create()

    fun loadFromAssets(name: String): MoveTemplate {
        val inputStream = javaClass.getResourceAsStream("/assets/${PokemonCobbled.MODID}/moves/$name.json")!!
        val moveTemplate = GSON.fromJson<MoveTemplate>(InputStreamReader(inputStream))
        moveTemplate.createTextComponents()
        return moveTemplate
    }
}