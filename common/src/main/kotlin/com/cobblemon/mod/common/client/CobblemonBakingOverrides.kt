package com.cobblemon.mod.common.client

import com.cobblemon.mod.common.BakingOverride
import com.cobblemon.mod.common.util.cobblemonModel
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.util.ModelIdentifier
import net.minecraft.util.Identifier

/**
 * The purpose of this class is to hold models that we want baked, but aren't associated with
 * any block state. Actual registration happens in ModelLoaderMixin/ModelLoader
 */
object CobblemonBakingOverrides {
    val models = mutableListOf<BakingOverride>()

    val FOSSIL_TUB_LIQUID = registerOverride(
        cobblemonResource("block/fossil_bubbling_liquid"),
        cobblemonModel("fossil_bubbling_liquid", "none")
    )

    fun registerOverride(modelLocation: Identifier, modelIdentifier: ModelIdentifier): BakingOverride {
        val result = BakingOverride(modelLocation, modelIdentifier)
        models.add(result)
        return result
    }
}