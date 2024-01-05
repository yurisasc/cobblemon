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
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BimanualFrame
import com.cobblemon.mod.common.client.render.models.blockbench.repository.RenderContext
import net.minecraft.util.math.MathHelper

/**
 * A bimanual arm animation that will have more force while moving and idle sway.
 * This creates a simple predictable arm movement like Minecraft bimanuals.
 *
 * @author Deltric
 * @since December 21st, 2021
 */
class BimanualSwingAnimation(
    /** The multiplier to apply to the cosine movement of the arms. The smaller this value, the quicker the arms move. */
    val swingPeriodMultiplier: Float = 0.6662F,
    /** The multiplier to apply to the swing of the entity. The larger this is, the further the arms move. */
    val amplitudeMultiplier: Float = 1F
) : StatelessAnimation() {
    override fun setAngles(context: RenderContext, model: PosableModel, state: PosableState, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float, headYaw: Float, headPitch: Float, intensity: Float) {
        val frame = model as? BimanualFrame ?: return
        // Movement swing
        frame.rightArm.yaw += MathHelper.cos(limbSwing * swingPeriodMultiplier) * limbSwingAmount * amplitudeMultiplier * intensity
        frame.leftArm.yaw += MathHelper.cos(limbSwing * swingPeriodMultiplier) * limbSwingAmount * amplitudeMultiplier * intensity

        // Idle sway
        frame.rightArm.roll += 1.0f * (MathHelper.cos(ageInTicks * 0.09f) * 0.05f + 0.05f) * intensity
        frame.rightArm.yaw += 1.0f * MathHelper.sin(ageInTicks * 0.067f) * 0.05f * intensity
        frame.leftArm.roll += -1.0f * (MathHelper.cos(ageInTicks * 0.09f) * 0.05f + 0.05f) * intensity
        frame.leftArm.yaw += -1.0f * MathHelper.sin(ageInTicks * 0.067f) * 0.05f * intensity
    }
}