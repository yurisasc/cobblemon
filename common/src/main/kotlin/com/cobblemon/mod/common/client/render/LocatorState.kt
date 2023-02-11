/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render

import net.minecraft.util.math.Matrix4f
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vector4f

/**
 * Holds onto the current rotation matrix for a model locator. I may change this to just be the Matrix4f and
 * extension functions since we'll use this in other ways.
 *
 * @author Hiroku
 * @since February 10th, 2023
 */
class LocatorState {
    var rotationMatrix: Matrix4f = Matrix4f()

    fun update(rotationMatrix: Matrix4f): LocatorState {
        this.rotationMatrix = rotationMatrix.copy()
        return this
    }

    fun getOrigin(): Vec3d {
        return transformPosition(Vec3d.ZERO)
    }

    fun transformPosition(pos: Vec3d): Vec3d {
        val vector = Vector4f(pos.x.toFloat(), pos.y.toFloat(), pos.z.toFloat(), 1F)
        vector.transform(rotationMatrix)
        vector.normalizeProjectiveCoordinates()
        return Vec3d(vector.x.toDouble(), vector.y.toDouble(), vector.z.toDouble())
    }

    fun transformDirection(direction: Vec3d): Vec3d {
        val origin = Vector4f(0F, 0F, 0F, 1F)
        origin.transform(rotationMatrix)
        val magnitude = direction.length()
        val vector = Vector4f(direction.x.toFloat(), direction.y.toFloat(), direction.z.toFloat(), 1F)
        vector.transform(rotationMatrix)
        vector.add(-origin.x, -origin.y, -origin.z, 0F)
        vector.multiply(magnitude.toFloat())
        vector.normalizeProjectiveCoordinates()
        return Vec3d(vector.x.toDouble(), vector.y.toDouble(), vector.z.toDouble())
    }
}