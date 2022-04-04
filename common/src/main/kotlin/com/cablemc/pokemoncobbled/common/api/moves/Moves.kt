package com.cablemc.pokemoncobbled.common.api.moves

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.asset.JsonManifestWalker
import com.cablemc.pokemoncobbled.common.api.moves.adapters.DamageCategoryAdapter
import com.cablemc.pokemoncobbled.common.api.moves.categories.DamageCategory
import com.cablemc.pokemoncobbled.common.api.types.ElementalType
import com.cablemc.pokemoncobbled.common.api.types.adapters.ElementalTypeAdapter
import com.google.gson.GsonBuilder

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
    fun getByNameOrDummy(name: String) = allMoves[name.lowercase()] ?: MoveTemplate.dummy(name.lowercase())
    fun getExceptional() = getByName("struggle") ?: allMoves.values.random()
    fun count() = allMoves.size

    /**
     * Loads the move JSON files into a HashMap.
     */
    fun loadFromFiles() : MutableMap<String, MoveTemplate> {
        val map = mutableMapOf<String, MoveTemplate>()
        try {
            val moveTemplates = JsonManifestWalker.load(
                MoveTemplate::class.java,
                "moves",
                GSON
            )
            for (template in moveTemplates) {
                map[template.name] = template
            }
            return map
        } catch (e: Exception) {
            PokemonCobbled.LOGGER.error("Error loading moves from files.")
            e.printStackTrace()
        }
        return mutableMapOf()
    }
}