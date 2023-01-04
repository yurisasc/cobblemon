package com.cobblemon.mod.common.client.particle

import com.bedrockk.molang.runtime.MoLangRuntime
import com.cobblemon.mod.common.api.snowstorm.BedrockParticleEffect

/**
 * An instance of a bedrock particle effect.
 *
 * @author Hiroku
 * @since January 2nd, 2022
 */
class ParticleStorm(val effect: BedrockParticleEffect) {
    val runtime = MoLangRuntime()
    // todo list of living particles
    var started = false
    var age = 0F

    fun tick(delta: Float) {
        age += delta
        val toEmit = effect.emitter.rate.getEmitCount(this)
    }

    fun emitTimes(times: Int) {

    }
}
