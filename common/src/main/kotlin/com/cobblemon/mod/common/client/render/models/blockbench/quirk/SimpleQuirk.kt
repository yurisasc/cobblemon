/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.quirk

import com.cobblemon.mod.common.client.render.models.blockbench.PosableState
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatefulAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.repository.RenderContext
import com.cobblemon.mod.common.util.math.random
import kotlin.random.Random

class SimpleQuirk(
    name: String,
    private val secondsBetweenOccurrences: Pair<Float, Float>,
    val condition: (context: RenderContext) -> Boolean = { true },
    val loopTimes: IntRange = 1..1,
    val animations: (state: PosableState) -> Iterable<StatefulAnimation>
) : ModelQuirk<SimpleQuirkData>(name) {
    override fun createData(): SimpleQuirkData = SimpleQuirkData(name)
    override fun tick(context: RenderContext, state: PosableState, data: SimpleQuirkData) {
        if (data.animations.isNotEmpty()) {
            return
        }

        if (!condition(context)) {
            return
        }

        if (data.remainingLoops > 0) {
            data.animations.addAll(animations(state))
            data.remainingLoops--
        }

        if (data.remainingLoops == 0) {
            if (data.nextOccurrenceSeconds > 0F) {
                // Is it time?
                if (data.nextOccurrenceSeconds <= state.animationSeconds) {
                    data.remainingLoops = loopTimes.random() - 1
                    data.animations.addAll(animations(state))
                    data.nextOccurrenceSeconds = -1F
                }
            } else {
                data.nextOccurrenceSeconds = state.animationSeconds + Random.nextFloat() * secondsBetweenOccurrences.random()
            }
        }
    }
}