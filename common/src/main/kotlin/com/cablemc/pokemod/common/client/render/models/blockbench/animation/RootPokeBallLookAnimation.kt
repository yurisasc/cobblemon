/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.render.models.blockbench.animation

import com.cablemc.pokemod.common.client.render.models.blockbench.PoseableEntityModel
import com.cablemc.pokemod.common.client.render.models.blockbench.PoseableEntityState
import com.cablemc.pokemod.common.client.render.models.blockbench.frame.PokeBallFrame
import com.cablemc.pokemod.common.entity.pokeball.EmptyPokeBallEntity
import net.minecraft.util.math.MathHelper.PI
import net.minecraft.util.math.MathHelper.atan2

/**
 * A simple idle animation that will point the PokéBall in the direction of the target it hit. Does nothing
 * for occupied PokéBalls.
 *
 * @author Hiroku
 * @since December 25th, 2021
 */
class RootPokeBallLookAnimation(frame: PokeBallFrame) : StatelessAnimation<EmptyPokeBallEntity, PokeBallFrame>(frame) {
    override val targetFrame = PokeBallFrame::class.java
    override fun setAngles(entity: EmptyPokeBallEntity?, model: PoseableEntityModel<EmptyPokeBallEntity>, state: PoseableEntityState<EmptyPokeBallEntity>?, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float, headYaw: Float, headPitch: Float) {
        val yRot = entity?.let {
            val dispX = it.hitTargetPosition.get().x - it.x
            val dispZ = it.hitTargetPosition.get().z - it.z
            atan2(-dispZ, dispX) + PI / 2
        }?.toFloat() ?: 0F

        model.rootPart.yaw = yRot
    }
}