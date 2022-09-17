/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.api.spawning.condition

import com.cablemc.pokemoncobbled.common.api.spawning.context.SpawningContext

/**
 * A basic spawning condition that works for any type of spawning context.
 *
 * @author Hiroku
 * @since February 7th, 2022
 */
class BasicSpawningCondition : SpawningCondition<SpawningContext>() {
    override fun contextClass(): Class<out SpawningContext> = SpawningContext::class.java
    companion object {
        const val NAME = "basic"
    }
}