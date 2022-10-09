/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.render.models.blockbench.pose

import com.cablemc.pokemod.common.client.render.models.blockbench.PoseableEntityModel
import com.cablemc.pokemod.common.client.render.models.blockbench.PoseableEntityState
import com.cablemc.pokemod.common.client.render.models.blockbench.animation.StatelessAnimation
import com.cablemc.pokemod.common.client.render.models.blockbench.frame.ModelFrame
import com.cablemc.pokemod.common.client.render.models.blockbench.quirk.ModelQuirk
import com.cablemc.pokemod.common.entity.PoseType
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
    fun idleStateless(model: PoseableEntityModel<T>, state: PoseableEntityState<T>?, limbSwing: Float = 0F, limbSwingAmount: Float = 0F, ageInTicks: Float = 0F, headYaw: Float = 0F, headPitch: Float = 0F) {
        idleAnimations.forEach { it.apply(null, model, state, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch) }
    }

    fun idleStateful(entity: T?, model: PoseableEntityModel<T>, state: PoseableEntityState<T>, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float, headYaw: Float, headPitch: Float) {
        idleAnimations.forEach { idleAnimation ->
            val allStatefuls = state.statefulAnimations + state.quirks.flatMap { it.value.animations }
            if (allStatefuls.none { it.preventsIdle(entity, state, idleAnimation) }) {
                idleAnimation.apply(entity, model, state, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch)
            }
        }
    }
}