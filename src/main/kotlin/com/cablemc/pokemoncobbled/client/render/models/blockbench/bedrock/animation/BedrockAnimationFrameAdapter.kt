package com.cablemc.pokemoncobbled.client.render.models.blockbench.bedrock.animation

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.mojang.math.Vector3d
import java.lang.reflect.Type

object BedrockAnimationFrameAdapter : JsonDeserializer<BedrockAnimationFrameSchema> {

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): BedrockAnimationFrameSchema {
        val rotations = deserializePropertyMap(json, "rotation")
        val positions = deserializePropertyMap(json, "position")
        val scales = deserializePropertyMap(json, "scale")
        return BedrockAnimationFrameSchema(
            rotationsByKeyFrame = rotations,
            positionsByKeyFrame = positions,
            scalarsByKeyFrame = scales
        )
    }

    private fun deserializePropertyMap(json: JsonElement, property: String): Map<Double, Vector3d> {
        val map = mutableMapOf<Double, Vector3d>()
        if (json.asJsonObject.has(property)) {
            json.asJsonObject[property].asJsonObject.entrySet().forEach { entry ->
                val jsonArray = entry.value.asJsonArray
                map[entry.key.toDouble()] = Vector3d(jsonArray[0].asDouble, jsonArray[1].asDouble, jsonArray[2].asDouble)
            }
        }
        return map
    }

}