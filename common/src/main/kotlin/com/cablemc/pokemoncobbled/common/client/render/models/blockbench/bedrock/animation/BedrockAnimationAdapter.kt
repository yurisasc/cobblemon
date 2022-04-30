package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.bedrock.animation

import com.eliotlash.molang.MolangParser
import com.google.gson.*
import net.minecraft.util.math.Vec3d
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
                deserializeMolangBoneValue(bone["position"].asJsonArray)
            }
        } else {
            EmptyBoneValue
        }
        val rotations = if (bone.has("rotation")) {
            if (bone["rotation"].isJsonObject) {
                deserializeRotationKeyframes(bone["rotation"].asJsonObject, Transformation.ROTATION)
            } else {
                deserializeMolangBoneValue(bone["rotation"].asJsonArray)
            }
        } else {
            EmptyBoneValue
        }
        return BedrockBoneTimeline(positions, rotations)
    }

    fun deserializeMolangBoneValue(array: JsonArray): MolangBoneValue {
        return MolangBoneValue(
            x = molangParser.parseExpression(array[0].asString),
            y = molangParser.parseExpression(array[1].asString),
            z = molangParser.parseExpression(array[2].asString)
        )
    }

    private fun deserializeRotationKeyframes(frames: JsonObject, transformation: Transformation): BedrockKeyFrameBoneValue {
        val keyframes = BedrockKeyFrameBoneValue()
        frames.entrySet().forEach { (time, keyframeJson) ->
            val timeDbl = time.toDouble()
            when {
                keyframeJson is JsonObject -> {
                    if (keyframeJson.has("post")) {
                        val transformationData = keyframeJson["post"].asJsonArray.map { it.asDouble }
                        val transformationVector = Vec3d(transformationData[0], transformationData[1], transformationData[2])
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
                    val transformationVector = Vec3d(transformationData[0], transformationData[1], transformationData[2])
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