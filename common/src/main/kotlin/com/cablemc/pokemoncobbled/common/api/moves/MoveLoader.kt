package com.cablemc.pokemoncobbled.common.api.moves

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.moves.adapters.DamageCategoryAdapter
import com.cablemc.pokemoncobbled.common.api.moves.categories.DamageCategory
import com.cablemc.pokemoncobbled.common.api.types.ElementalType
import com.cablemc.pokemoncobbled.common.api.types.adapters.ElementalTypeAdapter
import com.cablemc.pokemoncobbled.common.util.AssetLoading
import com.cablemc.pokemoncobbled.common.util.AssetLoading.toPath
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.stream.JsonReader
import java.io.InputStreamReader
import kotlin.io.path.inputStream

object MoveLoader {
//    val dirPath = Path("data/moves/")
    val GSON = GsonBuilder()
        .registerTypeAdapter(DamageCategory::class.java, DamageCategoryAdapter)
        .registerTypeAdapter(ElementalType::class.java, ElementalTypeAdapter)
        .setLenient()
        .disableHtmlEscaping()
        .create()

    /**
     * Loads the move JSON files into a HashMap.
     */
    fun loadFromFiles() : HashMap<String, MoveTemplate> {
        val map = HashMap<String, MoveTemplate>()

        val moveFolder = cobbledResource("moves")
        val path = moveFolder.toPath() ?: throw IllegalArgumentException("There is no valid, internal path for $moveFolder")
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

    /**
     * Remaps the data:
     * - Removes unused keys
     * - Renames the keys
     */
    private fun remap(key: String, jsonObj: JsonObject) : JsonObject? {
        try {
            val newJson = JsonObject()
            newJson.addProperty(NAME, key)
            newJson.add(TYPE, jsonObj.get("type"))
            newJson.add(DAMAGE_CATEGORY, jsonObj.get("category"))
            newJson.add(TARGET, jsonObj.get("target"))
            newJson.add(POWER, jsonObj.get("basePower"))
            if (jsonObj.get("accuracy").asJsonPrimitive.isBoolean) // Some have boolean... why?
                newJson.addProperty(ACCURACY, 100.0)
            else
                newJson.add(ACCURACY, jsonObj.get("accuracy"))
            newJson.add(PP, jsonObj.get("pp"))
            newJson.add(PRIORITY, jsonObj.get("priority"))
            newJson.add(CRIT_RATIO, jsonObj.get("critRatio"))
            try {
                newJson.add(EFFECT_CHANCE, jsonObj.get("secondary").asJsonObject.get("chance"))
                newJson.add(EFFECT_STATUS, jsonObj.get("secondary").asJsonObject.get("status"))
            } catch (_: Exception) { }
            newJson.add(FLAGS, jsonObj.get("flags"))
            return newJson
        } catch (e: Exception) {
            PokemonCobbled.LOGGER.error("Error remapping move: " + jsonObj.get("name"))
        }
        return null
    }

    /**
     * The JSON keys.
     */
    private const val NAME = "name"
    private const val TYPE = "type"
    private const val DAMAGE_CATEGORY = "damageCategory"
    private const val TARGET = "target"
    private const val POWER = "power"
    private const val ACCURACY = "accuracy"
    private const val PP = "pp"
    private const val PRIORITY = "priority"
    private const val CRIT_RATIO = "critRatio"
    private const val EFFECT_CHANCE = "effectChance"
    private const val EFFECT_STATUS = "effectStatus"
    private const val FLAGS = "flags"

}