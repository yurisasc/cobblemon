/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.npc

import com.bedrockk.molang.Expression
import com.cobblemon.mod.common.api.molang.ExpressionLike
import com.cobblemon.mod.common.client.render.models.blockbench.JsonPosableModel
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatefulAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.ModelFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Bone
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Pose
import com.cobblemon.mod.common.client.render.models.blockbench.repository.RenderContext
import com.cobblemon.mod.common.entity.npc.NPCEntity
import com.cobblemon.mod.common.util.adapters.ExpressionAdapter
import com.cobblemon.mod.common.util.adapters.ExpressionLikeAdapter
import com.cobblemon.mod.common.util.adapters.Vec3dAdapter
import com.google.gson.GsonBuilder
import com.google.gson.InstanceCreator
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.function.Supplier
import net.minecraft.util.math.Vec3d

class JsonNPCModel(override val rootPart: Bone) : JsonPosableModel(rootPart) {
    object JsonNPCModelAdapter : InstanceCreator<JsonNPCModel> {
        var modelPart: Bone? = null
        var model: JsonNPCModel? = null
        override fun createInstance(type: Type): JsonNPCModel {
            return JsonNPCModel(modelPart!!).also {
                model = it
                it.loadAllNamedChildren(modelPart!!)
            }
        }
    }

    companion object {
        var model: JsonNPCModel? = null
        val gson = GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .registerTypeAdapter(Vec3d::class.java, Vec3dAdapter)
            .setExclusionStrategies(JsonModelExclusion)
            .registerTypeAdapter(
                StatefulAnimation::class.java,
                StatefulAnimationAdapter { JsonNPCModelAdapter.model!! }
            )
            .registerTypeAdapter(
                Pose::class.java,
                PoseAdapter(
                    { json ->
                        val conditions = mutableListOf<(RenderContext) -> Boolean>()
                        if (json.has("isBattle")) {
                            conditions.add { (it.request(RenderContext.ENTITY) as? NPCEntity)?.isInBattle() == true }
                        } else if (json.has("isNotBattle")) {
                            conditions.add { (it.request(RenderContext.ENTITY) as? NPCEntity)?.isInBattle() == false }
                        }
                        conditions
                    },
                    { JsonNPCModelAdapter.model!! }
                ))
            .registerTypeAdapter(
                JsonNPCModel::class.java,
                JsonNPCModelAdapter
            )
            .registerTypeAdapter(Expression::class.java, ExpressionAdapter)
            .registerTypeAdapter(ExpressionLike::class.java, ExpressionLikeAdapter)
            .create()
    }
}