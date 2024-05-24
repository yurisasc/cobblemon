/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pose

import com.cobblemon.mod.common.api.molang.ExpressionLike
import com.cobblemon.mod.common.client.render.models.blockbench.PosableModel
import com.cobblemon.mod.common.client.render.models.blockbench.PosableState
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatefulAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatelessAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.quirk.ModelQuirk
import com.cobblemon.mod.common.client.render.models.blockbench.repository.RenderContext
import com.cobblemon.mod.common.entity.PoseType

typealias CobblemonPose = Pose

/**
 * A pose for a model.
 */
class Pose(
    var poseName: String,
    val poseTypes: Set<PoseType>,
    val condition: ((RenderContext) -> Boolean)?,
    val onTransitionedInto: (PosableState) -> Unit = {},
    val transformTicks: Int,
    val animations: MutableMap<String, ExpressionLike> = mutableMapOf(),
    val idleAnimations: Array<StatelessAnimation>,
    val transformedParts: Array<ModelPartTransformation>,
    val quirks: Array<ModelQuirk<*>>
) {
    fun isSuitable(context: RenderContext) = condition?.invoke(context) ?: true

    val transitions = mutableMapOf<String, (Pose, Pose) -> StatefulAnimation>()

    fun idleStateful(context: RenderContext, model: PosableModel, state: PosableState, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float, headYaw: Float, headPitch: Float) {
        idleAnimations.filter { state.shouldIdleRun(it, 0F) }.forEach { idleAnimation ->
            idleAnimation.apply(context, model, state, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, state.getIdleIntensity(idleAnimation))
        }
    }
}