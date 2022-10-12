/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.render.models.blockbench.bedrock.animation

import com.cablemc.pokemod.common.Pokemod.LOGGER
import com.eliotlash.molang.MolangParser
import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.lang.reflect.Type

/**
 * Gson adapter for converting bedrock/blockbench json data into a friendlier object model.
 *
 * @author landonjw
 * @since  January 5, 2022
 */
object BedrockAnimationAdapter : JsonDeserializer<BedrockAnimation> {
    val molangParser = MolangParser()

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): BedrockAnimation {
        if (json is JsonObject) {
            val animationLength = json["animation_length"]?.asDouble ?: -1.0
            val shouldLoop = animationLength > 0 && json["loop"]?.asBoolean == true
            val boneTimelines = mutableMapOf<String, BedrockBoneTimeline>()
            json["bones"].asJsonObject.entrySet().forEach { (boneName, timeline) ->
                boneTimelines[boneName] = deserializeBoneTimeline(timeline.asJsonObject)
            }
            return BedrockAnimation(shouldLoop, animationLength, boneTimelines)
        }
        else {
            throw IllegalStateException("animation json could not be parsed")
        }
    }

    private fun deserializeBoneTimeline(bone: JsonObject): BedrockBoneTimeline {
        val positions = if (bone.has("position")) {
            if (bone["position"].isJsonObject) {
                deserializeRotationKeyframes(bone["position"].asJsonObject, Transformation.POSITION)
            } else {
                deserializeMolangBoneValue(bone["position"].asJsonArray, Transformation.POSITION)
            }
        } else {
            EmptyBoneValue
        }
        val rotations = if (bone.has("rotation")) {
            if (bone["rotation"].isJsonObject) {
                deserializeRotationKeyframes(bone["rotation"].asJsonObject, Transformation.ROTATION)
            } else {
                deserializeMolangBoneValue(bone["rotation"].asJsonArray, Transformation.ROTATION)
            }
        } else {
            EmptyBoneValue
        }
        return BedrockBoneTimeline(positions, rotations)
    }

    fun cleanExpression(value: String) =
        (if (value.startsWith("+")) value.substring(1) else value).let {
            if (it.startsWith("-(")) it.replaceFirst("-(", "-1*(") else it
        }.replace("*+", "*")

    fun deserializeMolangBoneValue(array: JsonArray, transformation: Transformation): MolangBoneValue {
        try {
            return MolangBoneValue(
                x = molangParser.parseExpression(cleanExpression(array[0].asString)),
                y = molangParser.parseExpression(cleanExpression(array[1].asString)),
                z = molangParser.parseExpression(cleanExpression(array[2].asString)),
                transformation = transformation
            )
        } catch (e: Exception) {
            LOGGER.error(array.joinToString { it.toString() })
            throw e
        }
    }

    private fun deserializeRotationKeyframes(frames: JsonObject, transformation: Transformation): BedrockKeyFrameBoneValue {
        val keyframes = BedrockKeyFrameBoneValue()
        frames.entrySet().forEach { (time, keyframeJson) ->
            val timeDbl = time.toDouble()
            when {
                keyframeJson is JsonObject -> {
                    if (keyframeJson.has("post")) {
//                        val transformationData = keyframeJson["post"].asJsonArray.map { it.asDouble }
//                        val transformationVector = Vec3d(transformationData[0], transformationData[1], transformationData[2])
                        keyframes[timeDbl] = BedrockAnimationKeyFrame(
                            time = timeDbl,
                            transformation = transformation,
                            data = deserializeMolangBoneValue(keyframeJson["post"].asJsonArray, transformation),
                            interpolationType = InterpolationType.SMOOTH
                        )
                    }
                    else {
                        throw IllegalStateException("transformation data ('post') could not be found")
                    }
                }
                keyframeJson is JsonArray -> {
//                    val transformationData = keyframeJson.map { it.asDouble }
//                    val transformationVector = Vec3d(transformationData[0], transformationData[1], transformationData[2])
                    keyframes[timeDbl] = BedrockAnimationKeyFrame(
                        time = timeDbl,
                        transformation = transformation,
                        data = deserializeMolangBoneValue(keyframeJson, transformation),
                        interpolationType = InterpolationType.LINEAR
                    )
                }
                else -> throw IllegalStateException("keyframe json could not be parsed")
            }
        }
        return keyframes
    }

}