/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.render.models.blockbench.frame

import com.cablemc.pokemod.common.client.render.models.blockbench.PoseableEntityState
import com.cablemc.pokemod.common.client.render.models.blockbench.animation.WingFlapIdleAnimation
import com.cablemc.pokemod.common.client.render.models.blockbench.wavefunction.WaveFunction
import net.minecraft.client.model.ModelPart
import net.minecraft.entity.Entity

interface BiWingedFrame : ModelFrame {
    val leftWing: ModelPart
    val rightWing: ModelPart

    fun <T : Entity> wingFlap(
        flapFunction: WaveFunction,
        timeVariable: (state: PoseableEntityState<T>?, limbSwing: Float, ageInTicks: Float) -> Float?,
        axis: Int
    ) = WingFlapIdleAnimation(
        frame = this,
        flapFunction = flapFunction,
        timeVariable = timeVariable,
        axis = axis
    )
}