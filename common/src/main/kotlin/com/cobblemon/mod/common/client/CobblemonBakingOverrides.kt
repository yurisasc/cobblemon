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

    val FOSSIL_FLUID_BUBBLING = registerOverride(
        cobblemonResource("block/fossil_fluid_bubbling"),
        cobblemonModel("fossil_fluid_bubbling", "none")
    )
    val FOSSIL_FLUID_CHUNKED = registerOverride(
        cobblemonResource("block/fossil_fluid_chunked"),
        cobblemonModel("fossil_fluid_chunked", "none")
    )



    fun registerOverride(modelLocation: Identifier, modelIdentifier: ModelIdentifier): BakingOverride {
        val result = BakingOverride(modelLocation, modelIdentifier)
        models.add(result)
        return result
    }
}