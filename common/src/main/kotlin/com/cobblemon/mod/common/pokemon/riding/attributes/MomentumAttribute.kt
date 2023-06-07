/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.riding.attributes

import com.cobblemon.mod.common.api.riding.attributes.RidingAttribute
import com.cobblemon.mod.common.util.math.pow
import net.minecraft.util.math.Vec3d
import kotlin.math.min

data class MomentumAttribute(val speed: RidingAttribute, val acceleration: RidingAttribute) {

    private var currentSpeed = 0F
    private var tick : Int = 0

    fun tick(movement: Vec3d) {
        if (movement == Vec3d.ZERO) {
            this.reset()
        } else {
            ++tick

            val result = when(acceleration.value) {
                1F -> ONE(this.tick % 20)
                2F -> TWO(this.tick / 5)
                3F -> THREE(this.tick)
                4F -> FOUR(this.tick)
                else -> speed.value
            }

            this.currentSpeed = min(speed.value, result)
        }
    }

    fun momentum() : Float {
        return this.currentSpeed
    }

    private fun reset() {
        this.tick = 0
        this.currentSpeed = 0F
    }

    companion object AccelerationGraphs {

        val ONE: (Int) -> Float = { input -> 0.1F * input.pow(2) }
        val TWO: (Int) -> Float = { input -> 0.005F * input.pow(2) }
        val THREE: (Int) -> Float = { input -> input.pow(2).toFloat() }
        val FOUR: (Int) -> Float = { input -> 5F * input.pow(2) }

    }

}