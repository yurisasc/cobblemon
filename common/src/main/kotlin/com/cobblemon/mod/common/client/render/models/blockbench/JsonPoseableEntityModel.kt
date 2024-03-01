/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench

import com.bedrockk.molang.runtime.MoLangRuntime
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatefulAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Bone
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Pose
import com.cobblemon.mod.common.client.render.models.blockbench.repository.RenderContext
import com.cobblemon.mod.common.entity.Poseable
import com.cobblemon.mod.common.util.asExpressionLike
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

abstract class JsonPosableModel(override val rootPart: Bone) : PosableModel() {
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

    class JsonPoseableModelAdapter(val constructor: (modelPart: ModelPart) -> JsonPosableModel) : InstanceCreator<PosableModel> {
        var modelPart: ModelPart? = null
        var model: JsonPosableModel? = null
        override fun createInstance(type: Type): JsonPosableModel {
            return constructor(modelPart!!).also {
                model = it
                it.loadAllNamedChildren(modelPart!!)
            }
        }
    }

    class StatefulAnimationAdapter(val modelFinder: () -> PosableModel) : JsonDeserializer<Supplier<StatefulAnimation>> {
        override fun deserialize(json: JsonElement, type: Type, ctx: JsonDeserializationContext): Supplier<StatefulAnimation> {
            json as JsonPrimitive
            val animString = json.asString
            val splits = animString.replace("bedrock(", "").replace(")", "").split(",").map(String::trim)
            val file = splits[0]
            val animation = splits[1]
            return Supplier { modelFinder().bedrockStateful(file, animation) }
        }
    }

    class PoseAdapter(
        val poseConditionReader: (JsonObject) -> List<(RenderContext) -> Boolean>,
        val modelFinder: () -> PosableModel
    ) : JsonDeserializer<Pose> {
        companion object {
            val runtime = MoLangRuntime()
        }

        override fun deserialize(json: JsonElement, type: Type, ctx: JsonDeserializationContext): Pose {
            val model = modelFinder()
            val obj = json as JsonObject
            val pose = JsonPose(model, obj)

            val conditionsList = mutableListOf<(RenderContext) -> Boolean>()
            val mustBeTouchingWater = json.get("isTouchingWater")?.asBoolean
            if (mustBeTouchingWater != null) {
                conditionsList.add { mustBeTouchingWater == it.entity?.isTouchingWater }
            }
            conditionsList.addAll(poseConditionReader(json))

            if (json.has("condition")) {
                val condition = json.get("condition").asString
                conditionsList.add {
                    val entity = it.request(RenderContext.ENTITY)
                    if (entity is Poseable) {
                        runtime.environment.structs["query"] = entity.struct
                        condition.asExpressionLike().resolveBoolean(runtime)
                    } else {
                        false
                    }
                }
            }

            val poseCondition: (RenderContext) -> Boolean = if (conditionsList.isEmpty()) { { true } } else conditionsList.reduce { acc, function -> { acc(it) && function(it) } }

            return Pose(
                poseName = pose.poseName,
                poseTypes = pose.poseTypes.toSet(),
                condition = poseCondition,
                transformTicks = pose.transformTicks,
                idleAnimations = pose.idleAnimations,
                transformedParts = pose.transformedParts,
                quirks = pose.quirks.toTypedArray()
            ).also {
                it.transitions.putAll(
                    pose.transitions
                        .mapNotNull<JsonPose.JsonPoseTransition, Pair<String, (Pose, Pose) -> StatefulAnimation>> {
                            it.to to { _, _ -> it.animation.resolveObject(model.runtime).obj as StatefulAnimation }
                        }.toMap()
                )
            }
        }
    }
}