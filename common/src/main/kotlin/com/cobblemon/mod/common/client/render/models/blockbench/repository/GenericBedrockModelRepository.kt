package com.cobblemon.mod.common.client.render.models.blockbench.repository

import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityModel
import com.cobblemon.mod.common.client.render.models.blockbench.generic.JsonGenericPoseableModel
import com.cobblemon.mod.common.entity.generic.GenericBedrockEntity
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.model.ModelPart

object GenericBedrockModelRepository : VaryingModelRepository<GenericBedrockEntity, PoseableEntityModel<GenericBedrockEntity>>() {
    override val title = "Generic"
    override val type = "generic"
    override val variationDirectories: List<String> = listOf("bedrock/$type/variations")
    override val poserDirectories: List<String> = listOf("bedrock/$type/posers")
    override val modelDirectories: List<String> = listOf("bedrock/$type/models")
    override val animationDirectories: List<String> = listOf("bedrock/$type/animations")

    override val fallback = cobblemonResource("substitute")

    override fun registerInBuiltPosers() {}
    override fun loadJsonPoser(json: String): (ModelPart) -> PoseableEntityModel<GenericBedrockEntity> {
        return {
            JsonGenericPoseableModel.JsonGenericPoseableModelAdapter.modelPart = it
            JsonGenericPoseableModel.gson.fromJson(json, JsonGenericPoseableModel::class.java)
        }
    }
}