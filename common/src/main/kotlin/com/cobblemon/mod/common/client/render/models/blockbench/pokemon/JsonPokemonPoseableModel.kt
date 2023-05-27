/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon

import com.cobblemon.mod.common.client.render.models.blockbench.JsonPose
import com.cobblemon.mod.common.client.render.models.blockbench.JsonPoseableEntityModel
import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityModel
import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatefulAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.ModelFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Pose
import com.cobblemon.mod.common.client.render.models.blockbench.withPosition
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.util.adapters.Vec3dAdapter
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import com.google.gson.InstanceCreator
import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.function.Supplier
import net.minecraft.client.model.ModelPart
import net.minecraft.entity.Entity
import net.minecraft.util.math.Vec3d

/**
 * The goal of this is to allow a PokemonPoseableModel to be constructed from a JSON instead of being
 * created via a class. By being loadable from JSON, the full flow of a new Pokémon can be accomplished
 * from a resource + data pack.
 *
 * @author Hiroku
 * @since August 7th, 2022
 */
class JsonPokemonPoseableModel(override val rootPart: ModelPart) : PokemonPoseableModel(), HeadedFrame {
    companion object {
        val gson = GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .registerTypeAdapter(Vec3d::class.java, Vec3dAdapter)
            .setExclusionStrategies(JsonPoseableEntityModel.JsonModelExclusion)
            .registerTypeAdapter(
                TypeToken.getParameterized(
                    Supplier::class.java,
                    TypeToken.getParameterized(
                        StatefulAnimation::class.java,
                        PokemonEntity::class.java,
                        ModelFrame::class.java
                    ).type
                ).type,
                JsonPoseableEntityModel.StatefulAnimationAdapter { JsonPokemonPoseableModelAdapter.model!! }
            )
            .registerTypeAdapter(Pose::class.java, PoseAdapter)
            .registerTypeAdapter(JsonPokemonPoseableModel::class.java, JsonPokemonPoseableModelAdapter)
            .create()
    }

    override fun registerPoses() {}

    @SerializedName("head")
    val headJoint: String? = null

    override val head: ModelPart by lazy { headJoint?.let { getPart(it) } ?: rootPart }


    @SerializedName("portraitScale")
    private val _portraitScale: Float? = null
    @SerializedName("portraitTranslation")
    private val _portraitTranslation: Vec3d? = null

    @SerializedName("profileScale")
    private val _profileScale: Float? = null
    @SerializedName("profileTranslation")
    private val _profileTranslation: Vec3d? = null

    override val portraitScale
        get() = _portraitScale ?: 1F
    override val portraitTranslation
        get() = _portraitTranslation ?: Vec3d(0.0, 0.0, 0.0)

    override val profileScale
        get() = _profileScale ?: 1F
    override val profileTranslation
        get() = _profileTranslation ?: Vec3d(0.0, 0.0, 0.0)



    val faint: Supplier<StatefulAnimation<PokemonEntity, ModelFrame>>? = null

    override fun getFaintAnimation(pokemonEntity: PokemonEntity, state: PoseableEntityState<PokemonEntity>) = faint?.get()


    object JsonPokemonPoseableModelAdapter : InstanceCreator<JsonPokemonPoseableModel> {
        var modelPart: ModelPart? = null
        var model: JsonPokemonPoseableModel? = null
        override fun createInstance(type: Type): JsonPokemonPoseableModel {
            return JsonPokemonPoseableModel(modelPart!!).also {
                model = it
                it.loadAllNamedChildren(modelPart!!)
            }
        }
    }

    object PoseAdapter : JsonDeserializer<Pose<PokemonEntity, ModelFrame>> {
        override fun deserialize(json: JsonElement, type: Type, ctx: JsonDeserializationContext): Pose<PokemonEntity, ModelFrame> {
            val model = JsonPokemonPoseableModelAdapter.model!!
            val obj = json as JsonObject
            val pose = JsonPose(model, obj)

            val conditionsList = mutableListOf<(PokemonEntity) -> Boolean>()
            val mustBeInBattle = json.get("isBattle")?.asBoolean
            if (mustBeInBattle != null) {
                conditionsList.add { mustBeInBattle == it.battleId.get().isPresent }
            }
            val mustBeTouchingWater = json.get("isTouchingWater")?.asBoolean
            if (mustBeTouchingWater != null) {
                conditionsList.add { mustBeTouchingWater == it.isTouchingWater }
            }

            val poseCondition: (PokemonEntity) -> Boolean = if (conditionsList.isEmpty()) { { true } } else conditionsList.reduce { acc, function -> { acc(it) && function(it) } }

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