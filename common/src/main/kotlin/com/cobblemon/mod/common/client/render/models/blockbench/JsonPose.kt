/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench

import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatefulAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.entity.PoseType
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.minecraft.entity.Entity
import net.minecraft.util.math.Vec3d

class JsonPose<T : Entity>(model: PoseableEntityModel<T>, json: JsonObject) {
    val poseName = json.get("poseName").asString
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
                model.singleBoneLook<T>()
            } else {
                object : HeadedFrame {
                    override val rootPart = model.rootPart
                    override val head = model.getPartFallback("head_ai", "head")
                }.singleBoneLook()
            }
        } else if (animString.startsWith("bedrock")) {
            val split = animString.replace("bedrock(", "").replace(")", "").split(",").map(String::trim)
            return@mapNotNull model.bedrock(animationGroup = split[0], animation = split[1])
        }
        return@mapNotNull null
    }.toTypedArray()

    val quirks = (json.get("quirks")?.asJsonArray ?: JsonArray()).map { json ->
        json as JsonObject
        val name = json.get("name").asString
        val animations: (state: PoseableEntityState<T>) -> List<StatefulAnimation<T, *>> = { _ ->
            (json.get("animations")?.asJsonArray ?: JsonArray()).map { animJson ->
                val split =
                    animJson.asString.replace("bedrock(", "").replace(")", "").split(",").map(String::trim)
                model.bedrockStateful(animationGroup = split[0], animation = split[1]).setPreventsIdle(false)
            }
        }

        val loopTimes = json.get("loopTimes")?.asInt ?: 1
        val minSeconds = json.get("minSecondsBetweenOccurrences")?.asFloat ?: 8F
        val maxSeconds = json.get("maxSecondsBetweenOccurrences")?.asFloat ?: 30F

        model.quirkMultiple(
            name = name,
            secondsBetweenOccurrences = minSeconds to maxSeconds,
            condition = { true },
            loopTimes = 1..loopTimes,
            animations = animations
        )
    }
}