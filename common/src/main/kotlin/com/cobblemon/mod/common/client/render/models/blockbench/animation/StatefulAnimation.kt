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
import com.cobblemon.mod.common.client.render.models.blockbench.frame.ModelFrame
import com.cobblemon.mod.common.client.render.models.blockbench.repository.RenderContext

/**
 * An animation that requires entity state. It is able to prevent some idle
 * animations, usually on the basis of what [ModelFrame] class the animation
 * uses.
 *
 * @author Hiroku
 * @since December 5th, 2021
 */
interface StatefulAnimation {
    val isTransform: Boolean
    val duration: Float
    /** Runs the animation. You can check that the model fits a particular frame. Returns true if the animation should continue. */
    fun run(
        context: RenderContext,
        model: PosableModel,
        state: PosableState,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        headYaw: Float,
        headPitch: Float,
        intensity: Float
    ): Boolean

    fun applyEffects(context: RenderContext, state: PosableState, previousSeconds: Float, newSeconds: Float) {}
}