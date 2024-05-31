/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.blockentity

import com.cobblemon.mod.common.client.render.models.blockbench.PosableModel
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Bone
import com.google.gson.annotations.SerializedName
import net.minecraft.client.model.ModelPart

class BlockEntityModel(root: Bone) : PosableModel(root) {
    @Transient
    @SerializedName("dummy")
    override var isForLivingEntityRenderer = false
    @Transient
    @SerializedName("Don't bloody deserialize this, Gson! I mean it!")
    override val rootPart = (root as ModelPart).children.entries.first().let { root.registerChildWithAllChildren(it.key) }
//    val boneName: String = root.children.entries.first().key
//    @Transient
//    override val rootPart = (root as ModelPart).registerChildWithAllChildren(boneName)

    var maxScale = 1F
    var yTranslation = 0F

//    override fun registerPoses() {
//        val closedPose = registerPose(poseName = "CLOSED", poseType = PoseType.NONE)
//        val openPose = registerPose(
//            poseType = PoseType.OPEN,
//            idleAnimations = arrayOf(bedrock("gilded_chest", "open"))
//        )
//
//        closedPose.transitions[openPose.poseName] = { _, _ ->
//            bedrockStateful("gilded_chest", "opening")
//        }
//
//        openPose.transitions[closedPose.poseName] = { _, _ ->
//            bedrockStateful("gilded_chest", "closing")
//        }
//    }
}