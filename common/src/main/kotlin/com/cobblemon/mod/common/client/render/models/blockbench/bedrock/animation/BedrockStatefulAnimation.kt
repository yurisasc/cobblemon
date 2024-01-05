/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.bedrock.animation

import com.cobblemon.mod.common.client.render.models.blockbench.PosableModel
import com.cobblemon.mod.common.client.render.models.blockbench.PosableState
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatefulAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatelessAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.repository.RenderContext

/**
 * A stateful animation that runs a [BedrockAnimation]. It is completed when the underlying
 * bedrock animation is complete.
 *
 * @author Hiroku
 * @since July 16th, 2022
 */
open class BedrockStatefulAnimation(
    val animation: BedrockAnimation,
    var preventsIdleCheck: (RenderContext, PosableState, StatelessAnimation) -> Boolean
) : StatefulAnimation {
    fun setPreventsIdle(preventsIdle: Boolean): BedrockStatefulAnimation {
        this.preventsIdleCheck = { _, _, _ -> preventsIdle }
        return this
    }

    var startedSeconds = -1F
    var isTransformAnimation = false
    override val duration = animation.animationLength.toFloat()
    private var afterAction: (RenderContext, PosableState) -> Unit = { _, _ -> }

    override val isTransform: Boolean
        get() = isTransformAnimation

    fun isTransformAnimation(value: Boolean) = this.also {
        it.isTransformAnimation = value
    }

    fun andThen(action: (RenderContext, PosableState) -> Unit) = this.also {
        it.afterAction = action
    }

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
        if (startedSeconds == -1F) {
            startedSeconds = state.animationSeconds
        }

        return animation.run(context, model, state, state.animationSeconds - startedSeconds, intensity).also {
            if (!it) {
                afterAction(context, state)
            }
        }
    }

    override fun applyEffects(context: RenderContext, state: PosableState, previousSeconds: Float, newSeconds: Float) {
        val previousSecondsOffset = previousSeconds - startedSeconds
        val currentSecondsOffset = newSeconds - startedSeconds
        animation.applyEffects(context, state, previousSecondsOffset, currentSecondsOffset)
    }
}