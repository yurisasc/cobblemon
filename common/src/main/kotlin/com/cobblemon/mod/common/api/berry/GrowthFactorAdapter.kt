package com.cobblemon.mod.common.api.berry

import com.cobblemon.mod.common.util.adapters.CobblemonGrowthFactorAdapter
import com.google.gson.JsonDeserializer
import com.google.gson.JsonSerializer
import net.minecraft.util.Identifier
import kotlin.reflect.KClass

/**
 * A type adapter for [GrowthFactor]s.
 * For the default implementation see [CobblemonGrowthFactorAdapter].
 *
 * @author Licious
 * @since December 2nd, 2022
 */
interface GrowthFactorAdapter : JsonDeserializer<GrowthFactor>, JsonSerializer<GrowthFactor> {

    fun register(type: KClass<out GrowthFactor>, identifier: Identifier)

}