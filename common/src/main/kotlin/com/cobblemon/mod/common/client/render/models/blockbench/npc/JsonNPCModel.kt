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
import com.cobblemon.mod.common.client.entity.NPCClientDelegate
import com.cobblemon.mod.common.client.render.models.blockbench.JsonPoseableEntityModel
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatefulAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.ModelFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Bone
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Pose
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

class JsonNPCModel(override val rootPart: Bone, override val isForLivingEntityRenderer: Boolean = true) : JsonPoseableEntityModel<NPCEntity>(rootPart) {
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
                TypeToken.getParameterized(
                    Supplier::class.java,
                    TypeToken.getParameterized(
                        StatefulAnimation::class.java,
                        NPCEntity::class.java,
                        ModelFrame::class.java
                    ).type
                ).type,
                StatefulAnimationAdapter { JsonNPCModelAdapter.model!! }
            )
            .registerTypeAdapter(
                Pose::class.java,
                PoseAdapter(
                    { json ->
                        val conditions = mutableListOf<(NPCEntity) -> Boolean>()
                        if (json.has("isBattle")) {
                            conditions.add { it.isInBattle() }
                        } else if (json.has("isNotBattle")) {
                            conditions.add { !it.isInBattle() }
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

    override fun getState(entity: NPCEntity) = entity.delegate as NPCClientDelegate
}