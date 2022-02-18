package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.bedrock.animation

import com.google.gson.*
import com.mojang.math.Vector3d
import java.lang.reflect.Type

/**
 * Gson adapter for converting bedrock/blockbench json data into a friendlier object model.
 *
 * @author landonjw
 * @since  January 5, 2022
 */
object BedrockAnimationAdapter : JsonDeserializer<BedrockAnimation> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): BedrockAnimation {
        if (json is JsonObject) {
            val shouldLoop = if (json.has("loop")) json["loop"].asBoolean else false
            val animationLength = json["animation_length"].asDouble
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
            deserializeRotationKeyframes(bone["position"].asJsonObject, Transformation.POSITION)
        }
        else {
            emptyMap()
        }
        val rotations = if (bone.has("rotation")) {
            deserializeRotationKeyframes(bone["rotation"].asJsonObject, Transformation.ROTATION)
        }
        else {
            emptyMap()
        }
        return BedrockBoneTimeline(positions, rotations)
    }

    private fun deserializeRotationKeyframes(frames: JsonObject, transformation: Transformation): Map<Double, BedrockAnimationKeyFrame> {
        val keyframes = mutableMapOf<Double, BedrockAnimationKeyFrame>()
        frames.entrySet().forEach { (time, keyframeJson) ->
            val timeDbl = time.toDouble()
            when {
                keyframeJson is JsonObject -> {
                    if (keyframeJson.has("post")) {
                        val transformationData = keyframeJson["post"].asJsonArray.map { it.asDouble }
                        val transformationVector = Vector3d(transformationData[0], transformationData[1], transformationData[2])
                        keyframes[timeDbl] = BedrockAnimationKeyFrame(
                            time = timeDbl,
                            transformation = transformation,
                            data = transformationVector,
                            interpolationType = InterpolationType.SMOOTH
                        )
                    }
                    else {
                        throw IllegalStateException("transformation data ('post') could not be found")
                    }
                }
                keyframeJson is JsonArray -> {
                    val transformationData = keyframeJson.map { it.asDouble }
                    val transformationVector = Vector3d(transformationData[0], transformationData[1], transformationData[2])
                    keyframes[timeDbl] = BedrockAnimationKeyFrame(
                            time = timeDbl,
                            transformation = transformation,
                            data = transformationVector,
                            interpolationType = InterpolationType.LINEAR
                    )
                }
                else -> throw IllegalStateException("keyframe json could not be parsed")
            }
        }
        return keyframes
    }

}