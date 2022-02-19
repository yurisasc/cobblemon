package com.cablemc.pokemoncobbled.common.util.adapters

import com.cablemc.pokemoncobbled.common.api.spawning.SpawnLoader
import com.cablemc.pokemoncobbled.common.api.spawning.condition.SpawningCondition
import com.cablemc.pokemoncobbled.common.api.spawning.context.SpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.detail.RegisteredSpawnDetail
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnDetail
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

/**
 * A simple map adapter for [SpawnDetail] implementations. The only unusual thing about this
 * map adapter is it places the detected [RegisteredSpawnDetail] in [SpawnLoader.deserializingRegisteredSpawnDetail]
 * which is used in the [ContextPropertyMapAdapter].
 *
 * @author Hiroku
 * @since January 31st, 2022
 */
object SpawnDetailAdapter : JsonDeserializer<SpawnDetail> {
    override fun deserialize(element: JsonElement, type: Type, ctx: JsonDeserializationContext): SpawnDetail {
        val spawnDetailTypeName = element.asJsonObject.get("type").asString
        val registeredSpawnDetail = SpawnDetail.spawnDetailTypes[spawnDetailTypeName]
            ?: throw IllegalStateException("Spawn detail type name '$spawnDetailTypeName' is not recognized.")
        val ctxName = element.asJsonObject.get("context").asString
        val ctxType = SpawningContext.getByName(ctxName)
            ?: throw IllegalStateException("Unrecognized context name: $ctxName")
        SpawnLoader.deserializingConditionClass = SpawningCondition.getByName(ctxType.defaultCondition)
            ?: throw IllegalStateException("There is no spawning condition registered with the name '${ctxType.defaultCondition}'")
        SpawnLoader.deserializingRegisteredSpawnDetail = registeredSpawnDetail
        val detail = registeredSpawnDetail.deserializeDetail(element, ctx)
        detail.autoLabel()
        return detail
    }
}