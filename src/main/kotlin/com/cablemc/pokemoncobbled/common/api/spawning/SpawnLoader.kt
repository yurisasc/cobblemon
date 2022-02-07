package com.cablemc.pokemoncobbled.common.api.spawning

import com.cablemc.pokemoncobbled.common.api.spawning.condition.SpawningCondition
import com.cablemc.pokemoncobbled.common.api.spawning.condition.TimeRange
import com.cablemc.pokemoncobbled.common.api.spawning.context.RegisteredSpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.detail.RegisteredSpawnDetail
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnDetail
import com.cablemc.pokemoncobbled.common.util.adapters.BiomeListAdapter
import com.cablemc.pokemoncobbled.common.util.adapters.RegisteredSpawningContextAdapter
import com.cablemc.pokemoncobbled.common.util.adapters.ResourceLocationAdapter
import com.cablemc.pokemoncobbled.common.util.adapters.SpawnDetailAdapter
import com.cablemc.pokemoncobbled.common.util.adapters.SpawningConditionAdapter
import com.cablemc.pokemoncobbled.common.util.adapters.TimeRangeAdapter
import com.google.gson.GsonBuilder
import net.minecraft.resources.ResourceLocation

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

    fun loadFromFolder(path: String): List<SpawnDetail> {
        TODO("Load sets and details")
    }
}