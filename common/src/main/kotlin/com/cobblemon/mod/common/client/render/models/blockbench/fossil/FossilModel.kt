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
import com.cobblemon.mod.common.entity.PoseType
import net.minecraft.entity.Entity

class FossilModel(override val rootPart: Bone) : PoseableEntityModel<Entity>() {
    override val isForLivingEntityRenderer = false
    var maxScale = 1F
    var yTranslation = 0F
    var animations: Array<StatelessAnimation<Entity, out ModelFrame>> = emptyArray()

    override fun registerPoses() {
        registerPose(
            poseType = PoseType.SLEEP,
            idleAnimations = animations
        )
    }

    override fun getState(entity: Entity) = throw NotImplementedError("This is not supported for fossil models")
}