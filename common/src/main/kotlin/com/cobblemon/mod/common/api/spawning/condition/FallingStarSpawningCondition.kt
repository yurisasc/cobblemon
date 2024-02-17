/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.spawning.condition

import com.cobblemon.mod.common.api.spawning.context.FallingStarSpawningContext

class FallingStarSpawningCondition: SpawningCondition<FallingStarSpawningContext>() {
    override fun contextClass() = FallingStarSpawningContext::class.java

    override fun fits(ctx: FallingStarSpawningContext): Boolean {
        return true
    }

    companion object {
        const val NAME = "fallingstar"
    }
}