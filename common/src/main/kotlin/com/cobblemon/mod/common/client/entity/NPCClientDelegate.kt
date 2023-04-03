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
import com.cobblemon.mod.common.entity.npc.NPCEntity

class NPCClientDelegate : PoseableEntityState<NPCEntity>(), EntitySideDelegate<NPCEntity> {
    override fun initialize(entity: NPCEntity) {
    }

    override fun tick(entity: NPCEntity) {
        super.tick(entity)
        updateLocatorPosition(entity.pos)
    }
}