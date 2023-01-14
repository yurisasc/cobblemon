package com.cobblemon.mod.common.client.particle

import com.bedrockk.molang.runtime.MoLangRuntime
import com.bedrockk.molang.runtime.value.DoubleValue
import com.cobblemon.mod.common.api.snowstorm.BedrockParticleEffect
import kotlin.random.Random

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

    var x = 0
    var y = 0
    var z = 0

    fun tick(delta: Float) {
        runtime.environment.setValue("variable.emitter_random_1", DoubleValue(Random.Default.nextDouble()))
        runtime.environment.setValue("variable.emitter_random_2", DoubleValue(Random.Default.nextDouble()))
        runtime.environment.setValue("variable.emitter_random_3", DoubleValue(Random.Default.nextDouble()))
        runtime.environment.setValue("variable.emitter_random_4", DoubleValue(Random.Default.nextDouble()))
        age += delta
//        val toEmit = effect.emitter.rate.getEmitCount(this)
    }

    fun emitTimes(times: Int) {

    }
}
