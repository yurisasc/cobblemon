/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench

import com.bedrockk.molang.runtime.MoLangRuntime
import com.cobblemon.mod.common.api.molang.ExpressionLike
import com.cobblemon.mod.common.api.molang.MoLangFunctions.addFunctions
import com.cobblemon.mod.common.api.molang.MoLangFunctions.getQueryStruct
import com.cobblemon.mod.common.api.molang.MoLangFunctions.setup
import com.cobblemon.mod.common.client.ClientMoLangFunctions.setupClient
import com.bedrockk.molang.runtime.MoLangRuntime
import com.cobblemon.mod.common.api.molang.ExpressionLike
import com.cobblemon.mod.common.api.molang.MoLangFunctions.addFunctions
import com.cobblemon.mod.common.api.molang.MoLangFunctions.getQueryStruct
import com.cobblemon.mod.common.api.molang.MoLangFunctions.setup
import com.cobblemon.mod.common.client.ClientMoLangFunctions.setupClient
import com.cobblemon.mod.common.client.render.models.blockbench.animation.SingleBoneLookAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatefulAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatelessAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.quirk.SimpleQuirk
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.util.asExpressionLike
import com.cobblemon.mod.common.util.resolveObject
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.google.gson.JsonPrimitive
import net.minecraft.util.math.Vec3d

class JsonPose(model: PosableModel, json: JsonObject) {
    class JsonPoseTransition(val from: String, val to: String, val animation: ExpressionLike)

    val runtime = MoLangRuntime().setup().setupClient().also {
        it.environment.getQueryStruct().addFunctions(model.functions.functions)
    }

    val poseName = json.get("poseName")?.asString ?: "pose"
    val poseTypes = (json.get("poseTypes")?.asJsonArray?.map { name ->
        PoseType.values().find { it.name.lowercase() == name.asString.lowercase() }
            ?: throw IllegalArgumentException("Unknown pose type ${name.asString}")
    } ?: emptyList()) + if (json.get("allPoseTypes")?.asBoolean == true) PoseType.values().toList() else emptyList()
    val transformTicks = json.get("transformTicks")?.asInt ?: 10
    val transformedParts = json.get("transformedParts")?.asJsonArray?.map {
        it as JsonObject
        val partName = it.get("part").asString
        val part = model.getPart(partName).createTransformation()
        val rotation = it.get("rotation")?.asJsonArray?.let { Vec3d(it[0].asDouble, it[1].asDouble, it[2].asDouble) } ?: Vec3d.ZERO
        val position = it.get("position")?.asJsonArray?.let { Vec3d(it[0].asDouble, it[1].asDouble, it[2].asDouble) } ?: Vec3d.ZERO
        val isVisible = it.get("isVisible")?.asBoolean ?: true
        return@map part.withPosition(position.x, position.y, position.z).withRotationDegrees(rotation.x, rotation.y, rotation.z).withVisibility(isVisible)
    }?.toTypedArray() ?: arrayOf()

    val idleAnimations = (json.get("animations")?.asJsonArray ?: JsonArray()).asJsonArray.mapNotNull {
        val animString = it.asString
        if (animString == "look") {
            return@mapNotNull if (model is HeadedFrame) {
                model.singleBoneLook()
            } else {
                SingleBoneLookAnimation(bone = model.relevantPartsByName["head_ai"] ?: model.relevantPartsByName["head"])
            }
        } else if (animString.startsWith("bedrock")) {
            val split = animString.replace("bedrock(", "").replace(")", "").split(",").map(String::trim)
            return@mapNotNull model.bedrock(animationGroup = split[0], animation = split[1])
        } else {
            try {
                val expression = animString.asExpressionLike()
                return@mapNotNull runtime.resolveObject(expression).obj as StatelessAnimation
            } catch (exception: Exception) {
                return@mapNotNull null
            }
        }
        return@mapNotNull null
    }.toTypedArray()

    val quirks = (json.get("quirks")?.asJsonArray ?: JsonArray()).map { json ->
        if (json is JsonPrimitive) {
            return@map json.asString.asExpressionLike().resolveObject(runtime).obj as SimpleQuirk
        }

        json as JsonObject
        val animations: (state: PosableState) -> List<StatefulAnimation> = { _ ->
            (json.get("animations")?.asJsonArray ?: JsonArray()).map { animJson ->
                try {
                    val expr = animJson.asString.asExpressionLike()
                    runtime.resolveObject(expr).obj as StatefulAnimation
                } catch (e: Exception) {
                    val split =
                        animJson.asString.replace("bedrock(", "").replace(")", "").split(",").map(String::trim)
                    model.bedrockStateful(animationGroup = split[0], animation = split[1])
                }
            }
        }

        val loopTimes = json.get("loopTimes")?.asInt ?: 1
        val minSeconds = json.get("minSecondsBetweenOccurrences")?.asFloat ?: 8F
        val maxSeconds = json.get("maxSecondsBetweenOccurrences")?.asFloat ?: 30F

        model.quirkMultiple(
            secondsBetweenOccurrences = minSeconds to maxSeconds,
            condition = { true },
            loopTimes = 1..loopTimes,
            animations = animations
        )
    }

    val animations = json.get("namedAnimations")?.takeIf { it is JsonObject }?.asJsonObject?.let {
        val map = mutableMapOf<String, ExpressionLike>()
        for ((key, value) in it.entrySet()) {
            map[key] = value.asString.asExpressionLike()
        }
        map
    } ?: mutableMapOf()

    val transitions = json.get("transitions")?.takeIf { it is JsonArray }?.asJsonArray?.map {
        it as JsonObject
        val from = it.get("from").asString
        val to = it.get("to").asString
        val animation = it.get("animation").asString.asExpressionLike()
        JsonPoseTransition(from, to, animation)
    } ?: emptyList()
}