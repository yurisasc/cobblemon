package com.cablemc.pokemoncobbled.common.api.spawning.detail

import com.cablemc.pokemoncobbled.common.api.spawning.ContextProperties
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement

/**
 * A [SpawnDetail] implementation that has been registered. It is registered alongside
 * a [ContextProperties] implementation so that specific context properties can be made
 * for the [SpawnDetail] type.
 *
 * @author Hiroku
 * @since January 31st, 2022
 */
class RegisteredSpawnDetail<T : SpawnDetail, U : ContextProperties>(
    val detailClass: Class<T>,
    val contextPropertyClass: Class<U>
) {
    fun deserializeDetail(element: JsonElement, ctx: JsonDeserializationContext): T = ctx.deserialize(element, detailClass)
    fun deserializeContextProperty(element: JsonElement, ctx: JsonDeserializationContext): U = ctx.deserialize(element, contextPropertyClass)
}