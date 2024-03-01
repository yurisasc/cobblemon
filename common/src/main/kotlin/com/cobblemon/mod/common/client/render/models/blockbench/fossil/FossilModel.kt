/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.fossil

import com.cobblemon.mod.common.client.render.models.blockbench.PosableModel
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatelessAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Bone
import com.cobblemon.mod.common.client.render.models.blockbench.quirk.ModelQuirk
import com.cobblemon.mod.common.entity.PoseType
import net.minecraft.client.model.ModelPart

class FossilModel(root: Bone) : PosableModel() {
    //TODO: Find a better way to fetch this bone name - Update the BlockEntityModel too when you figure it out
    val boneName: String = root.children.entries.first().key
    override val rootPart = (root as ModelPart).registerChildWithAllChildren(boneName)
    // Represents a very rough middle of the model
    // The reason to do this is to
    // 1. The embryo is aligned with the center of the model
    // 2. The model can appear to grow (scale) from the center out
    var yGrowthPoint = 0F
    var maxScale = 1F
    var yTranslation = 0F // Offset inside the tank
    var tankAnimations: Array<StatelessAnimation> = emptyArray()
    var tankQuirks: Array<ModelQuirk<*>> = emptyArray()
    override fun registerPoses() {
        registerPose(
            poseType = PoseType.SLEEP,
            idleAnimations = tankAnimations,
            quirks = tankQuirks
        )
    }
}