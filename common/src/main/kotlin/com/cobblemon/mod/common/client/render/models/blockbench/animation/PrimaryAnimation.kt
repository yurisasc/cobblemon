/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.animation

import com.cobblemon.mod.common.client.render.models.blockbench.PosableModel
import com.cobblemon.mod.common.client.render.models.blockbench.PosableState
import com.cobblemon.mod.common.client.render.models.blockbench.repository.RenderContext
import com.cobblemon.mod.common.client.render.models.blockbench.wavefunction.WaveFunction
import java.util.function.Consumer

/**
 * An animation that plays to the exclusion of other primary animations. This can control
 * whether other animations are blocked or faded in or out. It is able to prevent some idle
 * animations based on labels.
 *
 * @author Hiroku
 * @since November 20th, 2023
 */
class PrimaryAnimation(
    val animation: StatefulAnimation,
    var curve: WaveFunction = { t ->
//        parabolaFunction(0.5F, 0.5F, 0.5F)
          if (t < 0.1) {
              t * 10
          } else if (t < 0.9) {
              1F
          } else {
              1F
          }
    },
    val excludedLabels: Set<String> = emptySet(),
    override var isTransform: Boolean = false
): StatefulAnimation {
    var started = -1F
    override val duration: Float = animation.duration
    var afterAction: Consumer<Unit> = Consumer { }

    override fun run(
        context: RenderContext,
        model: PosableModel,
        state: PosableState,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        headYaw: Float,
        headPitch: Float,
        intensity: Float
    ): Boolean {
        return animation.run(context, model, state, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, intensity)
    }

    fun prevents(idleAnimation: StatelessAnimation) = idleAnimation.labels.intersect(excludedLabels).isEmpty() && "all" !in excludedLabels
}