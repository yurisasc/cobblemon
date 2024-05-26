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
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.AnimationReferenceFactory
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Bone
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Pose
import com.cobblemon.mod.common.entity.Poseable
import com.cobblemon.mod.common.util.asExpressionLike
import com.cobblemon.mod.common.util.isDusk
import com.cobblemon.mod.common.util.isStandingOnRedSand
import com.cobblemon.mod.common.util.isStandingOnSand
import com.cobblemon.mod.common.util.isStandingOnSandOrRedSand
import com.google.gson.GsonBuilder
import com.google.gson.InstanceCreator
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import java.lang.reflect.Type
import java.util.function.Supplier

/**
 * The goal of this is to allow a PosableModel to be constructed from a JSON instead of being
 * created via a class. By being loadable from JSON, the full flow of a new entity can be accomplished
 * from a resource + data pack.
 *
 * @author Hiroku
 * @since August 7th, 2022
 */
class JsonPosableModel {
    class JsonModelAdapter<T : PosableModel>(private val constructor: (Bone) -> T) : InstanceCreator<T> {
        var modelPart: Bone? = null
        var model: T? = null
        override fun createInstance(type: Type): T {
            return constructor(modelPart!!).also {
                model = it
                it.loadAllNamedChildren(modelPart!!)
            }
        }
    }

    companion object {
        fun registerFactory(id: String, factory: AnimationReferenceFactory) {
            ANIMATION_FACTORIES[id] = factory
        }

        val ANIMATION_FACTORIES = mutableMapOf<String, AnimationReferenceFactory>()

        fun <T : PosableModel> createAdapter(poserClass: Class<T>): JsonModelAdapter<T> {
            return JsonModelAdapter { poserClass.getConstructor(Bone::class.java).newInstance(it) }
        }

        fun <T : PosableModel> GsonBuilder.setupGsonForJsonPosableModels(
            poserClass: Class<T>,
            adapter: JsonModelAdapter<T>,
            poseConditionReader: (JsonObject) -> List<(PosableState) -> Boolean> = { emptyList() }
        ): GsonBuilder {
            return this
                .excludeFieldsWithModifiers()
                .registerTypeAdapter(
                    StatefulAnimation::class.java,
                    StatefulAnimationAdapter { adapter.model!! }
                )
                .registerTypeAdapter(Pose::class.java, PoseAdapter(poseConditionReader) { adapter.model!! })
                .registerTypeAdapter(
                    poserClass,
                    adapter
                )
        }

    }

    class StatefulAnimationAdapter(val modelFinder: () -> PosableModel) : JsonDeserializer<Supplier<StatefulAnimation>> {
        override fun deserialize(json: JsonElement, type: Type, ctx: JsonDeserializationContext): Supplier<StatefulAnimation> {
            json as JsonPrimitive
            val animString = json.asString
            val format = animString.substringBefore("(")
            return Supplier { ANIMATION_FACTORIES[format]!!.stateful(modelFinder(), animString) }
        }
    }

    class PoseAdapter(
        val poseConditionReader: (JsonObject) -> List<(PosableState) -> Boolean>,
        val modelFinder: () -> PosableModel
    ) : JsonDeserializer<Pose> {
        companion object {
            val runtime = MoLangRuntime()
        }

        override fun deserialize(json: JsonElement, type: Type, ctx: JsonDeserializationContext): Pose {
            val model = modelFinder()
            val obj = json as JsonObject
            val pose = JsonPose(model, obj)

            val conditionsList = mutableListOf<(PosableState) -> Boolean>()
            val mustBeTouchingWater = json.get("isTouchingWater")?.asBoolean
            if (mustBeTouchingWater != null) {
                conditionsList.add { mustBeTouchingWater == it.getEntity()?.isTouchingWater }
            }

            val mustBeTouchingWaterOrRain = json.get("isTouchingWaterOrRain")?.asBoolean
            if (mustBeTouchingWaterOrRain != null) {
                conditionsList.add { mustBeTouchingWaterOrRain == it.getEntity()?.isTouchingWaterOrRain }
            }
            val mustBeSubmergedInWater = json.get("isSubmergedInWater")?.asBoolean
            if (mustBeSubmergedInWater != null) {
                conditionsList.add { mustBeSubmergedInWater == it.getEntity()?.isSubmergedInWater }
            }
            val mustBeStandingOnRedSand = json.get("isStandingOnRedSand")?.asBoolean
            if (mustBeStandingOnRedSand != null) {
                conditionsList.add { mustBeStandingOnRedSand == it.getEntity()?.isStandingOnRedSand() }
            }
            val mustBeStandingOnSand = json.get("isStandingOnSand")?.asBoolean
            if (mustBeStandingOnSand != null) {
                conditionsList.add { mustBeStandingOnSand == it.getEntity()?.isStandingOnSand() }
            }
            val mustBeStandingOnSandOrRedSand = json.get("isStandingOnSandOrRedSand")?.asBoolean
            if (mustBeStandingOnSandOrRedSand != null) {
                conditionsList.add { mustBeStandingOnSandOrRedSand == it.getEntity()?.isStandingOnSandOrRedSand() }
            }
            val mustBeDusk = json.get("isDusk")?.asBoolean
            if (mustBeDusk != null) {
                conditionsList.add { mustBeDusk == it.getEntity()?.isDusk() }
            }

            conditionsList.addAll(poseConditionReader(json))

            if (json.has("condition")) {
                val condition = json.get("condition").asString
                conditionsList.add {
                    val entity = it.getEntity()
                    if (entity is Poseable) {
                        runtime.environment.structs["query"] = entity.struct
                        condition.asExpressionLike().resolveBoolean(runtime)
                    } else {
                        false
                    }
                }
            }

            val poseCondition: (PosableState) -> Boolean = if (conditionsList.isEmpty()) { { true } } else conditionsList.reduce { acc, function -> { acc(it) && function(it) } }

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