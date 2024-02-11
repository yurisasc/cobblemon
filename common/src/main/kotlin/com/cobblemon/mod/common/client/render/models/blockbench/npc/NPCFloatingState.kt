/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.npc

import com.cobblemon.mod.common.api.scheduling.ClientTaskTracker
import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.entity.npc.NPCEntity

class NPCFloatingState : PoseableEntityState<NPCEntity>() {
    override fun getEntity() = null
    override val schedulingTracker = ClientTaskTracker
    override fun updatePartialTicks(partialTicks: Float) {
        this.currentPartialTicks += partialTicks
    }
}