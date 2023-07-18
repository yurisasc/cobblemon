/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.entity

import com.cobblemon.mod.common.api.entity.EntitySideDelegate
import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.models.blockbench.repository.GenericBedrockModelRepository
import com.cobblemon.mod.common.entity.generic.GenericBedrockEntity

class GenericBedrockClientDelegate : EntitySideDelegate<GenericBedrockEntity>, PoseableEntityState<GenericBedrockEntity>() {
    override fun initialize(entity: GenericBedrockEntity) {
        super.initialize(entity)
        this.age = entity.age
        this.currentModel = GenericBedrockModelRepository.getPoser(entity.category, entity.aspects)
        val pose = this.currentModel!!.poses.values.firstOrNull {
            (entity.getCurrentPoseType() in it.poseTypes) && it.condition(entity)
        }
        if (pose != null) {
            setPose(pose.poseName)
        }
        this.currentModel!!.updateLocators(entity, this)
//        entity.poseType.subscribe {
//            this.setPose()
//        }
    }

    override fun tick(entity: GenericBedrockEntity) {
        super.tick(entity)
        updateLocatorPosition(entity.pos)
        incrementAge(entity)
    }

    override fun updatePartialTicks(partialTicks: Float) {
        this.currentPartialTicks = partialTicks
    }
}