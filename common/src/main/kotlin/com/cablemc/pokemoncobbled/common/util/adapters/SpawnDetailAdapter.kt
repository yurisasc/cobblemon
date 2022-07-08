package com.cablemc.pokemoncobbled.common.util.adapters

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.PokemonCobbled.LOGGER
import com.cablemc.pokemoncobbled.common.api.spawning.SpawnLoader
import com.cablemc.pokemoncobbled.common.api.spawning.condition.SpawningCondition
import com.cablemc.pokemoncobbled.common.api.spawning.context.SpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnDetail
import com.cablemc.pokemoncobbled.common.api.spawning.preset.SpawnDetailPreset
import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.lang.reflect.Type

/**
 * A simple map adapter for [SpawnDetail] implementations. This adapter does some heavy logic to apply
 * [SpawnDetailPreset]s and to carefully derive the default subclass to be used when deserializing
 * [SpawningCondition]s.
 *
 * @author Hiroku
 * @since January 31st, 2022
 */
object SpawnDetailAdapter : JsonDeserializer<SpawnDetail> {
    override fun deserialize(element: JsonElement, type: Type, ctx: JsonDeserializationContext): SpawnDetail {
        element as JsonObject
        val presetNames = element.get("presets")?.asJsonArray?.map { it.asString }?.toMutableSet() ?: mutableSetOf()
        element.get("preset")?.let { presetNames.add(it.asString) }
        val presets = presetNames.mapNotNull {
            val preset = PokemonCobbled.bestSpawner.presets[it]
            if (preset == null) {
                LOGGER.error("Unknown preset name: $it.")
            }
            return@mapNotNull preset
        }
        val firstType = presets.firstNotNullOfOrNull { it.spawnDetailType }

        // Move `condition` into the `conditions`
        if (element.has("condition")) {
            if (!element.has("conditions")) {
                element.add("conditions", JsonArray())
            }
            element.get("conditions").asJsonArray.add(element.get("condition"))
            element.remove("condition")
        }

        // Move `anticondition` into the `anticonditions`
        if (element.has("anticondition")) {
            if (!element.has("anticonditions")) {
                element.add("anticonditions", JsonArray())
            }
            element.get("anticonditions").asJsonArray.add(element.get("anticondition"))
            element.remove("anticondition")
        }

        val spawnDetailTypeName = firstType
            ?: element.get("type")?.asString
            ?: throw IllegalStateException("Spawn detail type name not mentioned in either presets or in spawn detail.")
        val registeredSpawnDetail = SpawnDetail.spawnDetailTypes[spawnDetailTypeName]
            ?: throw IllegalStateException("Unrecognized spawn detail type name: $spawnDetailTypeName.")
        val ctxName = presets.firstNotNullOfOrNull { it.context?.name }
            ?: element.get("context").asString
        val ctxType = SpawningContext.getByName(ctxName)
            ?: throw IllegalStateException("Unrecognized context name: $ctxName")
        SpawnLoader.deserializingConditionClass = SpawningCondition.getByName(ctxType.defaultCondition)
            ?: throw IllegalStateException("There is no spawning condition registered with the name '${ctxType.defaultCondition}'")
        val detail = registeredSpawnDetail.deserializeDetail(element, ctx)
        presets.forEach { it.apply(detail) }
        if (detail.bucket.name.isBlank()) {
            throw IllegalStateException("No bucket was specified for spawn: ${detail.id}")
        }
        detail.autoLabel()
        return detail
    }
}