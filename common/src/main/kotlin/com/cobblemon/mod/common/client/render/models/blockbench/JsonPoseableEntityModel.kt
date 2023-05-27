package com.cobblemon.mod.common.client.render.models.blockbench

import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatefulAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.ModelFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.JsonPokemonPoseableModel
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Pose
import com.cobblemon.mod.common.entity.generic.GenericBedrockEntity
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.InstanceCreator
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import java.lang.reflect.Type
import java.util.function.Supplier
import net.minecraft.client.model.ModelPart
import net.minecraft.entity.Entity

abstract class JsonPoseableEntityModel<T : Entity>(override val rootPart: ModelPart) : PoseableEntityModel<T>() {
    override fun registerPoses() {}

    object JsonModelExclusion: ExclusionStrategy {
        override fun shouldSkipField(f: FieldAttributes): Boolean {
            return f.declaringClass.simpleName !in listOf(
                "JsonPokemonPoseableModel",
                "JsonGenericPoseableModel",
                "PoseableEntityModel",
                "Pose"
            )
        }

        override fun shouldSkipClass(clazz: Class<*>): Boolean {
            return false
        }
    }

    class JsonPoseableModelAdapter<T : Entity>(val constructor: (modelPart: ModelPart) -> JsonPoseableEntityModel<T>) : InstanceCreator<PoseableEntityModel<T>> {
        var modelPart: ModelPart? = null
        var model: JsonPoseableEntityModel<T>? = null
        override fun createInstance(type: Type): JsonPoseableEntityModel<T> {
            return constructor(modelPart!!).also {
                model = it
                it.loadAllNamedChildren(modelPart!!)
            }
        }
    }

    class StatefulAnimationAdapter<T : Entity>(val modelFinder: () -> PoseableEntityModel<T>) : JsonDeserializer<Supplier<StatefulAnimation<T, ModelFrame>>> {
        override fun deserialize(json: JsonElement, type: Type, ctx: JsonDeserializationContext): Supplier<StatefulAnimation<T, ModelFrame>> {
            json as JsonPrimitive
            val animString = json.asString
            val splits = animString.replace("bedrock(", "").replace(")", "").split(",").map(String::trim)
            val file = splits[0]
            val animation = splits[1]
            return Supplier { modelFinder().bedrockStateful(file, animation) }
        }
    }

    class PoseAdapter<T : Entity>(val modelFinder: () -> PoseableEntityModel<T>) : JsonDeserializer<Pose<T, ModelFrame>> {
        override fun deserialize(json: JsonElement, type: Type, ctx: JsonDeserializationContext): Pose<T, ModelFrame> {
            val model = modelFinder()
            val obj = json as JsonObject
            val pose = JsonPose(model, obj)

            val conditionsList = mutableListOf<(T) -> Boolean>()
            val mustBeTouchingWater = json.get("isTouchingWater")?.asBoolean
            if (mustBeTouchingWater != null) {
                conditionsList.add { mustBeTouchingWater == it.isTouchingWater }
            }

            val poseCondition: (T) -> Boolean = if (conditionsList.isEmpty()) { { true } } else conditionsList.reduce { acc, function -> { acc(it) && function(it) } }

            return Pose(
                poseName = pose.poseName,
                poseTypes = pose.poseTypes.toSet(),
                condition = poseCondition,
                transformTicks = pose.transformTicks,
                idleAnimations = pose.idleAnimations,
                transformedParts = pose.transformedParts,
                quirks = pose.quirks.toTypedArray()
            )
        }
    }
}