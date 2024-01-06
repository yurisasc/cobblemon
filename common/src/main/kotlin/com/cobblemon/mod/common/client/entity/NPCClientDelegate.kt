/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.entity

import com.cobblemon.mod.common.api.entity.NPCSideDelegate
import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.models.blockbench.animation.PrimaryAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.npc.NPCModel
import com.cobblemon.mod.common.entity.npc.NPCEntity

class NPCClientDelegate : PoseableEntityState<NPCEntity>(), NPCSideDelegate {
    lateinit var npcEntity: NPCEntity
    override val schedulingTracker
        get() = npcEntity.schedulingTracker
    override fun initialize(entity: NPCEntity) {
        this.npcEntity = entity
        this.age = entity.age
    }

    override fun tick(entity: NPCEntity) {
        super.tick(entity)
        updateLocatorPosition(entity.pos)
        incrementAge(entity)
    }

    override fun getEntity() = npcEntity

    override fun updatePartialTicks(partialTicks: Float) {
        this.currentPartialTicks = partialTicks
    }

    override fun playAnimation(animationType: String) {
        val currentModel = this.currentModel as? NPCModel ?: return
        val animation = currentModel.getAnimation(animationType)
        addPrimaryAnimation(PrimaryAnimation(animation, curve = { 1F }))
    }
}