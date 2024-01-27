/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.animation

import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityModel
import com.cobblemon.mod.common.client.render.models.blockbench.addRotation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.ModelFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Bone
import com.cobblemon.mod.common.client.render.models.blockbench.pose.ModelPartTransformation.Companion.X_AXIS
import com.cobblemon.mod.common.client.render.models.blockbench.pose.ModelPartTransformation.Companion.Y_AXIS
import com.cobblemon.mod.common.util.math.geometry.toRadians
import net.minecraft.entity.Entity

/**
 * A very simple animation for [HeadedFrame]s which has the entity look along the head yaw and pitch.
 * This is designed for simple entities where the model only needs to move a single bone to look at a
 * target.
 *
 * @author Hiroku
 * @since December 5th, 2021
 */
class SingleBoneLookAnimation<T : Entity>(
    frame: ModelFrame,
    val bone: Bone?,
    val pitchMultiplier: Float = 1F,
    val yawMultiplier: Float = 1F,
    val maxPitch: Float = 70F,
    val minPitch: Float = 45F,
    val maxYaw: Float = 45F,
) : StatelessAnimation<T, ModelFrame>(frame) {
    constructor(
        frame: HeadedFrame,
        invertX: Boolean,
        invertY: Boolean,
        disableX: Boolean,
        disableY: Boolean
    ): this(
        frame = frame,
        bone = frame.head,
        pitchMultiplier = if (disableX) 0F else if (invertX) -1F else 1F,
        yawMultiplier = if (disableY) 0F else if (invertY) -1F else 1F,
        maxPitch = if (invertX) -45F else 45F,
        minPitch = if (invertX) -70F else -45F,
        maxYaw = if (invertY) -45F else 45F,
    )


    override val targetFrame: Class<ModelFrame> = ModelFrame::class.java
    override var labels = setOf("look")
    override fun setAngles(entity: T?, model: PoseableEntityModel<T>, state: PoseableEntityState<T>?, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float, headYaw: Float, headPitch: Float, intensity: Float) {
        val head = bone ?: return
        val pitch = pitchMultiplier * headPitch.coerceIn(minPitch, maxPitch)
        val yaw = yawMultiplier * headYaw.coerceIn(-maxYaw, maxYaw)
        head.addRotation(X_AXIS, pitch.toRadians() * intensity)
        head.addRotation(Y_AXIS, yaw.toRadians() * intensity)
    }
}