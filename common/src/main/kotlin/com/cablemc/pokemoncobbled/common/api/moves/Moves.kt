package com.cablemc.pokemoncobbled.common.api.moves

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.moves.adapters.DamageCategoryAdapter
import com.cablemc.pokemoncobbled.common.api.moves.categories.DamageCategory
import com.cablemc.pokemoncobbled.common.api.types.ElementalType
import com.cablemc.pokemoncobbled.common.api.types.adapters.ElementalTypeAdapter
import com.cablemc.pokemoncobbled.common.util.AssetLoading
import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonReader
import java.io.InputStreamReader
import kotlin.io.path.inputStream
import kotlin.io.path.toPath

/**
 * Registry for all known Moves
 */
object Moves {
    val GSON = GsonBuilder()
        .registerTypeAdapter(DamageCategory::class.java, DamageCategoryAdapter)
        .registerTypeAdapter(ElementalType::class.java, ElementalTypeAdapter)
        .setLenient()
        .disableHtmlEscaping()
        .create()

    private val allMoves = mutableMapOf<String, MoveTemplate>()

    fun load() {
        allMoves.putAll(loadFromFiles())
    }

    fun getByName(name: String) = allMoves[name.lowercase()]
    fun count() = allMoves.size

    /**
     * Loads the move JSON files into a HashMap.
     */
    fun loadFromFiles() : MutableMap<String, MoveTemplate> {
        val map = mutableMapOf<String, MoveTemplate>()

        val path = PokemonCobbled::class.java.getResource("/assets/${PokemonCobbled.MODID}/moves")?.toURI()?.toPath()
            ?: throw IllegalArgumentException("There is no valid, internal path for /moves/")
        val internalPaths = AssetLoading.fileSearch(path, { it.toString().endsWith(".json") }, recursive = true)

        try {
            for (file in internalPaths) {
                val reader = JsonReader(InputStreamReader(file.inputStream()))
                val template = GSON.fromJson<MoveTemplate>(reader, MoveTemplate::class.java)
                map[template.name] = template
            }
            return map
        } catch (e: Exception) {
            PokemonCobbled.LOGGER.error("Error loading moves from files.")
            e.printStackTrace()
        }
        return HashMap()
    }
}