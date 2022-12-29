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

class SnowstormParticle {
    @SerializedName("format_version")
    var formatVersion = ""
    @SerializedName("particle_effect")
    var particleEffect = ParticleEffect()
}

class ParticleDescription {
    var identifier = Identifier("")
    @SerializedName("basic_render_parameters")
    var basicRenderParameters = BasicRenderParameters()
}

class ParticleCurve {
    val input: String = "v.name = 2;"
    @SerializedName("horizontal_range")
    val horizontalRange = "v.particle_lifetime"
    val nodes = arrayOf<Float>()
}

class BasicRenderParameters {
    val material = "particles_alpha"
    val texture = Identifier("")
}

enum class CurveType {
    LINEAR,
    BEZIER,
    CATMULL_ROM,
    BEZIER_CHAIN
}

class ParticleEffect {
    val description = ParticleDescription()
    val curves = mutableMapOf<String, ParticleCurve>()
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

enum class ParticleMaterial {
    ALPHA,
    OPAQUE,
    BLEND
}

