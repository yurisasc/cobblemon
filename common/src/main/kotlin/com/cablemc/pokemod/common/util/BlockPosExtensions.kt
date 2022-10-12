/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.util

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

/**
 * For conversion from BlockPos to Vec3d */
fun BlockPos.toVec3d(): Vec3d {
    return Vec3d(this.x.toDouble(), this.y.toDouble(), this.z.toDouble())
}