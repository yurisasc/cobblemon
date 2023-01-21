/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.snowstorm

import com.google.gson.annotations.SerializedName
import net.minecraft.util.Identifier

/*

particle_lifetime seems to be the variable that's basically q.anim_time in models

 */

enum class CurveType {
    LINEAR,
    BEZIER,
    CATMULL_ROM,
    BEZIER_CHAIN
}

class ParticleComponents {
    @SerializedName("emitter_initialization")
    val emitterInitialization: EmitterInitialization? = null
}

class EmitterInitialization {
    @SerializedName("creation_expression")
    val creationExpression = ""
    @SerializedName("per_update_expression")
    val perUpdateExpression = ""
}

class EmitterRateInstant {
    @SerializedName("num_particles")
    val numParticles = 0
}
class EmitterLifetimeLooping  {
    @SerializedName("active_time")
    var activeTime = 0
    @SerializedName("sleep_time")
    var sleepTime = 0
}
