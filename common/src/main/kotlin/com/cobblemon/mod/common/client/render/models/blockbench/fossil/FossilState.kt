/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.fossil

import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import net.minecraft.entity.Entity

/**
 * Floating state for a fossil Pokémon in the resurrection machine.
 *
 * @author Hiroku
 * @since October 30th, 2023
 */
class FossilState : PoseableEntityState<Entity>() {
    var totalPartialTicks = 0F
    override fun getEntity() = null
    override fun updatePartialTicks(partialTicks: Float) {
        this.totalPartialTicks += partialTicks
    }
}