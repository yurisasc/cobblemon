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
import net.minecraft.entity.Entity

/**
 * An animation that can run without an entity associated. These are
 * locked to a specific frame, and CAN be given an entity along with
 * limb swing and age information, but might not.
 *
 * @author Hiroku
 * @since December 4th, 2021
 */
abstract class StatelessAnimation {
    open var labels: Set<String> = setOf()
    protected abstract fun setAngles(
        context: RenderContext,
        model: PosableModel,
        state: PosableState,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        headYaw: Float,
        headPitch: Float,
        intensity: Float
    )

    fun apply(
        context: RenderContext,
        model: PosableModel,
        state: PosableState,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        headYaw: Float,
        headPitch: Float,
        intensity: Float
    ) {
        setAngles(context, model, state, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, intensity)
    }

    open fun applyEffects(entity: Entity, state: PosableState, previousSeconds: Float, newSeconds: Float) {}
}