/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.fossil

import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityModel
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatelessAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.ModelFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Bone
import com.cobblemon.mod.common.client.render.models.blockbench.quirk.ModelQuirk
import com.cobblemon.mod.common.entity.PoseType
import net.minecraft.client.model.ModelPart
import net.minecraft.entity.Entity

class FossilModel(root: Bone) : PoseableEntityModel<Entity>() {
    override val isForLivingEntityRenderer = false
    //TODO: Find a better way to fetch this bone name
    val boneName: String = root.children.entries.first().key
    override val rootPart = (root as ModelPart).registerChildWithAllChildren(boneName)

    var maxScale = 1F
    var yTranslation = 0F
    var tankAnimations: Array<StatelessAnimation<Entity, out ModelFrame>> = emptyArray()
    var tankQuirks: Array<ModelQuirk<Entity, *>> = emptyArray()
    override fun registerPoses() {
        registerPose(
            poseType = PoseType.SLEEP,
            idleAnimations = tankAnimations,
            quirks = tankQuirks
        )
    }

    override fun getState(entity: Entity) = throw NotImplementedError("This is not supported for fossil models")
}