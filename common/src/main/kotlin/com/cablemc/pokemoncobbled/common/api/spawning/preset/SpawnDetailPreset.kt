package com.cablemc.pokemoncobbled.common.api.spawning.preset

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.PokemonCobbled.LOGGER
import com.cablemc.pokemoncobbled.common.api.asset.JsonManifestWalker
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.cablemc.pokemoncobbled.common.api.spawning.BiomeLikeCondition
import com.cablemc.pokemoncobbled.common.api.spawning.SpawnBucket
import com.cablemc.pokemoncobbled.common.api.spawning.SpawnLoader
import com.cablemc.pokemoncobbled.common.api.spawning.condition.SpawningCondition
import com.cablemc.pokemoncobbled.common.api.spawning.condition.TimeRange
import com.cablemc.pokemoncobbled.common.api.spawning.context.RegisteredSpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnDetail
import com.cablemc.pokemoncobbled.common.api.spawning.multiplier.WeightMultiplier
import com.cablemc.pokemoncobbled.common.util.AssetLoading
import com.cablemc.pokemoncobbled.common.util.MergeMode
import com.cablemc.pokemoncobbled.common.util.adapters.*
import com.cablemc.pokemoncobbled.common.util.fromJson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import net.minecraft.tag.TagKey
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.world.biome.Biome

/**
 * Base class for spawn detail presets. Presets are a spawn loading mechanism that allows various properties to be
 * defined in a preset that will then be inserted into any spawn details that apply this preset. Presets can be used
 * to shortcut the process of commonly used conditions and other [SpawnDetail] properties as well as make those
 * commonly used properties very easy to maintain.
 *
 * A subclass of this base must be registered using [SpawnDetailPreset.registerPresetType].
 *
 * Preset loading occurs during initialization and first will load the internal presets. Then the external
 * config/pokemoncobbled/spawning/presets directory and its child directories will be searched for presets.
 * If a preset is loaded internally that has the same name as an external one, the external preset will take
 * precedence.
 *
 * Most of the logic for presets occurs inside the [SpawnDetailAdapter].
 *
 * It is worth understanding that these presets are purely a loading mechanism and don't exist from then on.
 *
 * @author Hiroku
 * @since July 8th, 2022
 */
abstract class SpawnDetailPreset {
    companion object {
        val GSON = GsonBuilder()
            .setPrettyPrinting()
            .setLenient()
            .disableHtmlEscaping()
            .registerTypeAdapter(SpawnBucket::class.java, SpawnBucketAdapter)
            .registerTypeAdapter(RegisteredSpawningContext::class.java, RegisteredSpawningContextAdapter)
            .registerTypeAdapter(BiomeLikeCondition::class.java, BiomeLikeConditionAdapter)
            .registerTypeAdapter(SpawnDetailPreset::class.java, SpawnDetailPresetAdapter)
            .registerTypeAdapter(Identifier::class.java, IdentifierAdapter)
            .registerTypeAdapter(
                TypeToken.getParameterized(TagKey::class.java, Biome::class.java).type,
                TagKeyAdapter<Biome>(Registry.BIOME_KEY)
            )
            .registerTypeAdapter(SpawningCondition::class.java, SpawningConditionAdapter)
            .registerTypeAdapter(TimeRange::class.java, TimeRangeAdapter)
            .registerTypeAdapter(PokemonProperties::class.java, pokemonPropertiesShortAdapter)

            .create()

        val presetTypes = mutableMapOf<String, Class<out SpawnDetailPreset>>()
        fun <T : SpawnDetailPreset> registerPresetType(name: String, detailClass: Class<T>) {
            presetTypes[name] = detailClass
        }

        fun load(): MutableMap<String, SpawnDetailPreset> {
            val map = mutableMapOf<String, SpawnDetailPreset>()
            map.putAll(loadInternal())
            map.putAll(loadExternal())
            return map
        }

        fun loadInternal(): MutableMap<String, SpawnDetailPreset> {
            val map = mutableMapOf<String, SpawnDetailPreset>()
            try {
                val presets = JsonManifestWalker.load(
                    SpawnDetailPreset::class.java,
                    "spawning/presets",
                    GSON
                )
                for (template in presets) {
                    map[template.name] = template
                }
                return map
            } catch (e: Exception) {
                LOGGER.error("Error loading internal spawn detail presets", e)
            }
            return mutableMapOf()
        }

        fun loadExternal(): MutableMap<String, SpawnDetailPreset> {
            val files = mutableListOf<File>()
            val map = mutableMapOf<String, SpawnDetailPreset>()
            AssetLoading.searchFor(
                dir = "config/${PokemonCobbled.MODID}/spawning/presets",
                suffix = ".json",
                list = files
            )
            files.forEach {
                try {
                    val reader = FileReader(it)
                    val preset = GSON.fromJson<SpawnDetailPreset>(reader)
                    reader.close()
                    map[preset.name] = preset
                } catch (e: Exception) {
                    LOGGER.error("Unable to load preset from file: ${it.name}", e)
                }
            }
            return map
        }
    }

    var name = ""
    var bucket: SpawnBucket? = null
    var spawnDetailType: String? = null
    var context: RegisteredSpawningContext<*>? = null
    var condition: JsonObject? = null
    var anticondition: JsonObject? = null
    var weightMultipliers: MutableList<WeightMultiplier>? = null
    var weight: Float? = null
    var percentage: Float? = null
    var mergeMode = MergeMode.REPLACE

    open fun apply(spawnDetail: SpawnDetail) {
        bucket?.let { spawnDetail.bucket = it }
        context?.let { spawnDetail.context = it }
        weight?.let { spawnDetail.weight = it }
        percentage?.let { spawnDetail.percentage = it }
        mergeMode.merge(spawnDetail.weightMultipliers, weightMultipliers)

        applyToConditionList(spawnDetail.conditions, condition?.let { resolveCondition(spawnDetail, it) })
        anticondition?.let { spawnDetail.anticonditions.add(resolveCondition(spawnDetail, it)) }
    }

    fun applyToConditionList(conditions: MutableList<SpawningCondition<*>>, resolvedCondition: SpawningCondition<*>?) {
        resolvedCondition ?: return
        conditions.forEach { it.copyFrom(resolvedCondition, mergeMode) }
        if (conditions.isEmpty()) {
            conditions.add(resolvedCondition)
        }
    }

    fun resolveCondition(spawnDetail: SpawnDetail, conditionJson: JsonObject): SpawningCondition<*> {
        SpawnLoader.deserializingConditionClass = SpawningCondition.getByName(spawnDetail.context.defaultCondition)
        return SpawnLoader.gson.fromJson(conditionJson, SpawningCondition::class.java)
    }
}