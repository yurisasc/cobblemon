/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pose

import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityModel
import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatefulAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatelessAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.ModelFrame
import com.cobblemon.mod.common.client.render.models.blockbench.quirk.ModelQuirk
import com.cobblemon.mod.common.entity.PoseType
import net.minecraft.entity.Entity

/**
 * A pose for a model.
 */
class Pose<T : Entity, F : ModelFrame>(
    val poseName: String,
    val poseTypes: Set<PoseType>,
    val condition: (T) -> Boolean,
    val onTransitionedInto: (PoseableEntityState<T>?) -> Unit = {},
    val transformTicks: Int,
    val idleAnimations: Array<StatelessAnimation<T, out F>>,
    val transformedParts: Array<TransformedModelPart>,
    val quirks: Array<ModelQuirk<T, *>>
) {
    val transitions = mutableMapOf<Pose<T, F>, (Pose<T, out ModelFrame>, Pose<T, out ModelFrame>) -> StatefulAnimation<T, ModelFrame>>()

    fun idleStateless(model: PoseableEntityModel<T>, state: PoseableEntityState<T>?, limbSwing: Float = 0F, limbSwingAmount: Float = 0F, ageInTicks: Float = 0F, headYaw: Float = 0F, headPitch: Float = 0F) {
        idleAnimations.forEach { it.apply(null, model, state, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch) }
    }

    fun idleStateful(entity: T?, model: PoseableEntityModel<T>, state: PoseableEntityState<T>, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float, headYaw: Float, headPitch: Float) {
        getApplicableIdleAnimations(entity, state).forEach { idleAnimation ->
            idleAnimation.apply(entity, model, state, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch)
        }
    }

    fun getApplicableIdleAnimations(entity: T?, state: PoseableEntityState<T>): List<StatelessAnimation<T, out F>> {
        val allStatefulAnimations = state.allStatefulAnimations
        return idleAnimations.filter { idle -> allStatefulAnimations.none { it.preventsIdle(entity, state, idle) } }
    }
}