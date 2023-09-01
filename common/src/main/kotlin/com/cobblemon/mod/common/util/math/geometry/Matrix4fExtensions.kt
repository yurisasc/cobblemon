/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.util.math.geometry

import net.minecraft.util.math.Vec3d
import org.joml.AxisAngle4d
import org.joml.Matrix4f
import org.joml.Vector3d
import org.joml.Vector4f

fun Matrix4f.getOrigin(): Vec3d {
    return transformPosition(Vec3d.ZERO)
}

fun Matrix4f.transformPosition(pos: Vec3d): Vec3d {
    val vector = Vector4f(pos.x.toFloat(), pos.y.toFloat(), pos.z.toFloat(), 1F)
    transform(vector)
    vector.mul(1 / vector.w)
    return Vec3d(vector.x.toDouble(), vector.y.toDouble(), vector.z.toDouble())
}

fun Matrix4f.transformDirection(direction: Vec3d): Vec3d {
    val rot = this.getRotation(AxisAngle4d())
    val v = rot.transform(Vector3d(direction.x, direction.y, direction.z))
    return Vec3d(v.x, v.y, v.z)
}