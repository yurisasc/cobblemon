/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.quirk

import com.cobblemon.mod.common.client.render.models.blockbench.PosableModel
import com.cobblemon.mod.common.client.render.models.blockbench.PosableState
import com.cobblemon.mod.common.client.render.models.blockbench.repository.RenderContext

abstract class ModelQuirk<D : QuirkData>(val name: String) {
    abstract fun createData(): D
    protected abstract fun tick(context: RenderContext, state: PosableState, data: D)
    fun tick(context: RenderContext, model: PosableModel, state: PosableState, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float, headYaw: Float, headPitch: Float, intensity: Float) {
        val data = getOrCreateData(state)
        tick(context, state, data)
        data.run(context, model, state, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, intensity)
    }
    fun getOrCreateData(state: PosableState): D {
        return state.quirks.getOrPut(this, this::createData) as D
    }
}