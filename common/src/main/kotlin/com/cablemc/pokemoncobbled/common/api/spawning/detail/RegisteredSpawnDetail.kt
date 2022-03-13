package com.cablemc.pokemoncobbled.common.api.spawning.detail

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement

/**
 * A [SpawnDetail] implementation that has been registered.
 *
 * @author Hiroku
 * @since January 31st, 2022
 */
class RegisteredSpawnDetail<T : SpawnDetail>(
    val detailClass: Class<T>
) {
    fun deserializeDetail(element: JsonElement, ctx: JsonDeserializationContext): T = ctx.deserialize(element, detailClass)
}