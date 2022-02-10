package com.cablemc.pokemoncobbled.common.api.spawning

import com.cablemc.pokemoncobbled.common.api.spawning.condition.SpawningCondition
import com.cablemc.pokemoncobbled.common.api.spawning.condition.TimeRange
import com.cablemc.pokemoncobbled.common.api.spawning.context.RegisteredSpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.detail.RegisteredSpawnDetail
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnDetail
import com.cablemc.pokemoncobbled.common.util.AssetLoading
import com.cablemc.pokemoncobbled.common.util.AssetLoading.toPath
import com.cablemc.pokemoncobbled.common.util.adapters.BiomeListAdapter
import com.cablemc.pokemoncobbled.common.util.adapters.RegisteredSpawningContextAdapter
import com.cablemc.pokemoncobbled.common.util.adapters.ResourceLocationAdapter
import com.cablemc.pokemoncobbled.common.util.adapters.SpawnDetailAdapter
import com.cablemc.pokemoncobbled.common.util.adapters.SpawningConditionAdapter
import com.cablemc.pokemoncobbled.common.util.adapters.TimeRangeAdapter
import com.cablemc.pokemoncobbled.common.util.fromJson
import com.cablemc.pokemoncobbled.mod.PokemonCobbledMod
import com.cablemc.pokemoncobbled.mod.config.CobbledConfig.useCustomSpawnFiles
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import net.minecraft.resources.ResourceLocation
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.file.Path

/**
 * Object responsible for actually deserializing spawns. You should probably
 * rely on this object for it as it would make your code better future proofed.
 *
 * @author Hiroku
 * @since January 31st, 2022
 */
object SpawnLoader {
    val gson = GsonBuilder()
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .setLenient()
        .registerTypeAdapter(BiomeList::class.java, BiomeListAdapter)
        .registerTypeAdapter(RegisteredSpawningContext::class.java, RegisteredSpawningContextAdapter)
        .registerTypeAdapter(ResourceLocation::class.java, ResourceLocationAdapter)
        .registerTypeAdapter(SpawnDetail::class.java, SpawnDetailAdapter)
        .registerTypeAdapter(SpawningCondition::class.java, SpawningConditionAdapter)
        .registerTypeAdapter(TimeRange::class.java, TimeRangeAdapter)
        .create()

    var deserializingRegisteredSpawnDetail: RegisteredSpawnDetail<*>? = null
    var deserializingConditionClass: Class<out SpawningCondition<*>>? = null

    fun loadInternalFolder(internalFolder: ResourceLocation): List<SpawnDetail> {
        val path = internalFolder.toPath() ?: throw IllegalArgumentException("There is no valid, internal path for $internalFolder")
        val internalPaths = AssetLoading.fileSearch(path, { it.endsWith(".json") }, recursive = true)
        val details = mutableListOf<SpawnDetail>()
        details.addAll(internalPaths.flatMap { loadInternalFile(it) })
        return details
    }

    fun loadInternalFile(path: Path): List<SpawnDetail> {
        if (path.endsWith(".packed.json")) {
            return loadPackedSpawnSet(path)
        }

        return emptyList()
    }

    fun loadPackedSpawnSet(path: Path): List<SpawnDetail> {
        val fileStream = javaClass.getResourceAsStream(path.toString())
            ?: throw IllegalArgumentException("Unable to create input stream for $path")

        val details = mutableListOf<SpawnDetail>()

        val sets = try {
            loadPackedSetsFromStream(fileStream)
        } catch (e: Exception) {
            PokemonCobbledMod.LOGGER.error(e.message)
            return emptyList()
        }

        println(path.toAbsolutePath().toString())
        if (useCustomSpawnFiles) {


            TODO("Loading an internal spawn set knowing that there will be external files")
        } else {
            details.addAll(sets.filter { it.isEnabled() }.flatMap { it.spawns })
        }

        return details
    }


    fun loadPackedSetsFromStream(inputStream: InputStream): List<SpawnSet> {
        val sets = mutableListOf<SpawnSet>()
        val jsonArray = gson.fromJson(InputStreamReader(inputStream), JsonArray::class.java)
        jsonArray.forEach { jsonElement ->
            try {
                sets.add(gson.fromJson<SpawnSet>(jsonElement))
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
        return sets
    }

    fun loadFromFolder(path: String): List<SpawnSet> {
        TODO("Load sets and details")
    }
}