package com.cobblemon.mod.common

import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.util.ModelIdentifier
import net.minecraft.util.Identifier

/**
 * Contains information for forcing a model to be baked
 *
 * @param modelLocation The location of the model
 * @param modelIdentifier The identifier that the BakedModel will be registered to
 */
data class BakingOverride(
    val modelLocation: Identifier,
    val modelIdentifier: ModelIdentifier
) {
    fun getModel(): BakedModel {
        return MinecraftClient.getInstance().bakedModelManager.getModel(modelIdentifier)
    }
}
