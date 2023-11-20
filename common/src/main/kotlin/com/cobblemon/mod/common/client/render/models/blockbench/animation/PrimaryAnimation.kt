/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.animation

import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityModel
import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.models.blockbench.wavefunction.WaveFunction
import net.minecraft.entity.Entity

class PrimaryAnimation<T : Entity>(
    val animation: StatefulAnimation<T, *>,
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
) {
    val duration = animation.duration
    var started = -1F

    fun run(
        entity: T?,
        model: PoseableEntityModel<T>,
        state: PoseableEntityState<T>,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        headYaw: Float,
        headPitch: Float,
        severity: Float
    ): Boolean {
        return animation.run(entity, model, state, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, severity)
    }

    fun prevents(idleAnimation: StatelessAnimation<T, *>) = idleAnimation.labels.intersect(excludedLabels).isEmpty() && "all" !in excludedLabels
}