/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.particle

import net.minecraft.util.math.Vec3d

interface SnowstormParticleOrigin {
    val position: Vec3d
}

class StaticParticleOrigin(override val position: Vec3d) : SnowstormParticleOrigin {
    constructor(x: Double, y: Double, z: Double): this(Vec3d(x, y, z))
}

//class EntityParticleOrigin(val entityId: Int): SnowstormParticleOrigin {
//    override val position: Vec3d
//        get() = MinecraftC
//}