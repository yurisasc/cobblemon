/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon

import com.cobblemon.mod.common.api.molang.ExpressionLike
import com.cobblemon.mod.common.client.entity.PokemonClientDelegate
import com.cobblemon.mod.common.client.render.models.blockbench.JsonPosableModel
import com.cobblemon.mod.common.client.render.models.blockbench.JsonPosableModel.StatefulAnimationAdapter
import com.cobblemon.mod.common.client.render.models.blockbench.JsonPose
import com.cobblemon.mod.common.client.render.models.blockbench.PosableState
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatefulAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Bone
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Pose
import com.cobblemon.mod.common.client.render.models.blockbench.repository.RenderContext
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.util.adapters.ExpressionLikeAdapter
import com.cobblemon.mod.common.util.adapters.Vec3dAdapter
import com.cobblemon.mod.common.util.isDusk
import com.cobblemon.mod.common.util.isStandingOnRedSand
import com.cobblemon.mod.common.util.isStandingOnSand
import com.cobblemon.mod.common.util.isStandingOnSandOrRedSand
import com.cobblemon.mod.common.util.resolveBoolean
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import com.google.gson.InstanceCreator
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type
import java.util.function.Supplier
import net.minecraft.util.math.Vec3d

/**
 * The goal of this is to allow a PosableModel to be constructed from a JSON instead of being
 * created via a class. By being loadable from JSON, the full flow of a new Pokémon can be accomplished
 * from a resource + data pack.
 *
 * @author Hiroku
 * @since August 7th, 2022
 */
class JsonPosableModel(rootPart: Bone) : JsonPosableModel(rootPart), HeadedFrame {
    companion object {
        val gson = GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .registerTypeAdapter(Vec3d::class.java, Vec3dAdapter)
            .setExclusionStrategies(JsonModelExclusion)
            .registerTypeAdapter(
                StatefulAnimation::class.java,
                StatefulAnimationAdapter { JsonPosableModelAdapter.model!! }
            )
            .registerTypeAdapter(ExpressionLike::class.java, ExpressionLikeAdapter)
            .registerTypeAdapter(Pose::class.java, PoseAdapter)
            .registerTypeAdapter(JsonPosableModel::class.java, JsonPosableModelAdapter)
            .create()

        fun registerFactory(id: String, factory: AnimationReferenceFactory) {
            ANIMATION_FACTORIES[id] = factory
        }

        val ANIMATION_FACTORIES = mutableMapOf<String, AnimationReferenceFactory>()
    }

    override fun registerPoses() {}

    @SerializedName("head")
    val headJoint: String? = null

    override val head: Bone by lazy { headJoint?.let { getPart(it) } ?: rootPart }

    val faint: Supplier<StatefulAnimation>? = null
    val cry: Supplier<StatefulAnimation>? = null

    object JsonModelExclusion: ExclusionStrategy {
        override fun shouldSkipField(f: FieldAttributes): Boolean {
            return f.declaringClass.simpleName !in listOf(
                "JsonPosableModel",
                "PoseableEntityModel",
                "Pose"
            )
        }

        override fun shouldSkipClass(clazz: Class<*>): Boolean {
            return false
        }

    }

    object StatefulAnimationAdapter : JsonDeserializer<Supplier<StatefulAnimation>> {
        override fun deserialize(json: JsonElement, type: Type, ctx: JsonDeserializationContext): Supplier<StatefulAnimation> {
            json as JsonPrimitive
            val animString = json.asString
            val format = animString.substringBefore("(")
            return Supplier { ANIMATION_FACTORIES[format]!!.stateful(JsonPosableModelAdapter.model!!, animString) }
        }
    }

    object JsonPosableModelAdapter : InstanceCreator<JsonPosableModel> {
        var modelPart: Bone? = null
        var model: JsonPosableModel? = null
        override fun createInstance(type: Type): JsonPosableModel {
            return JsonPosableModel(modelPart!!).also {
                model = it
                it.loadAllNamedChildren(modelPart!!)
            }
        }
    }

    object PoseAdapter : JsonDeserializer<Pose> {
        override fun deserialize(json: JsonElement, type: Type, ctx: JsonDeserializationContext): Pose {
            val model = JsonPosableModelAdapter.model!!
            val obj = json as JsonObject
            val pose = JsonPose(model, obj)

            val conditionsList = mutableListOf<(RenderContext) -> Boolean>()
            val mustBeInBattle = json.get("isBattle")?.asBoolean
            if (mustBeInBattle != null) {
                conditionsList.add { mustBeInBattle == (it.entity as? PokemonEntity)?.isBattling }
            }
            val mustBeTouchingWater = json.get("isTouchingWater")?.asBoolean
            if (mustBeTouchingWater != null) {
                conditionsList.add { mustBeTouchingWater == it.entity?.isTouchingWater }
            }
            val mustBeTouchingWaterOrRain = json.get("isTouchingWaterOrRain")?.asBoolean
            if (mustBeTouchingWaterOrRain != null) {
                conditionsList.add { mustBeTouchingWaterOrRain == it.entity?.isTouchingWaterOrRain }
            }
            val mustBeSubmergedInWater = json.get("isSubmergedInWater")?.asBoolean
            if (mustBeSubmergedInWater != null) {
                conditionsList.add { mustBeSubmergedInWater == it.entity?.isSubmergedInWater }
            }
            val mustBeStandingOnRedSand = json.get("isStandingOnRedSand")?.asBoolean
            if (mustBeStandingOnRedSand != null) {
                conditionsList.add { mustBeStandingOnRedSand == it.entity?.isStandingOnRedSand() }
            }
            val mustBeStandingOnSand = json.get("isStandingOnSand")?.asBoolean
            if (mustBeStandingOnSand != null) {
                conditionsList.add { mustBeStandingOnSand == it.entity?.isStandingOnSand() }
            }
            val mustBeStandingOnSandOrRedSand = json.get("isStandingOnSandOrRedSand")?.asBoolean
            if (mustBeStandingOnSandOrRedSand != null) {
                conditionsList.add { mustBeStandingOnSandOrRedSand == it.entity?.isStandingOnSandOrRedSand() }
            }
            val mustBeDusk = json.get("isDusk")?.asBoolean
            if (mustBeDusk != null) {
                conditionsList.add { mustBeDusk == it.entity?.isDusk() }
            }

            conditionsList.add { (it.runtime.resolveBoolean(pose.condition) }

            val poseCondition: ((RenderContext) -> Boolean)? = if (conditionsList.isEmpty()) null else conditionsList.reduce { acc, function -> { acc(it) && function(it) } }
            return Pose(
                poseName = pose.poseName,
                poseTypes = pose.poseTypes.toSet(),
                condition = poseCondition,
                animations = pose.animations,
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