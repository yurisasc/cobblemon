/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.particle

import com.bedrockk.molang.runtime.MoLangRuntime
import com.bedrockk.molang.runtime.struct.VariableStruct
import com.bedrockk.molang.runtime.value.DoubleValue
import com.cobblemon.mod.common.api.snowstorm.BedrockParticleEffect
import com.cobblemon.mod.common.api.snowstorm.ParticleEmitterAction
import com.cobblemon.mod.common.client.render.SnowstormParticle
import com.cobblemon.mod.common.particle.SnowstormParticleEffect
import kotlin.random.Random
import net.minecraft.client.particle.NoRenderParticle
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity

/**
 * An instance of a bedrock particle effect.
 *
 * @author Hiroku
 * @since January 2nd, 2022
 */
class ParticleStorm(
    val effect: BedrockParticleEffect,
    val origin: SnowstormParticleOrigin,
    val world: ClientWorld
): NoRenderParticle(world, origin.position.x, origin.position.y, origin.position.z) {
    val runtime = MoLangRuntime().also {
        it.environment.structs["variable"] = VariableStruct()
    }

    val particles = mutableListOf<SnowstormParticle>()
    var started = false
    var stopped = false

    var entity: Entity? = null

    companion object {
        val stormRegistry = mutableMapOf<BedrockParticleEffect, ParticleStorm>()
    }

    val particleEffect = SnowstormParticleEffect(effect)

    init {
        stormRegistry[effect] = this
        runtime.execute(effect.emitter.startExpressions)
    }

    override fun getMaxAge(): Int {
        return if (stopped) 0.also {
            stormRegistry.remove(effect)
        } else Int.MAX_VALUE
    }

    override fun tick() {
        setMaxAge(getMaxAge())
        super.tick()

        val pos = origin.position
        x = pos.x
        y = pos.y
        z = pos.z

        runtime.environment.setSimpleVariable("emitter_random_1", DoubleValue(Random.Default.nextDouble()))
        runtime.environment.setSimpleVariable("emitter_random_2", DoubleValue(Random.Default.nextDouble()))
        runtime.environment.setSimpleVariable("emitter_random_3", DoubleValue(Random.Default.nextDouble()))
        runtime.environment.setSimpleVariable("emitter_random_4", DoubleValue(Random.Default.nextDouble()))
        runtime.environment.setSimpleVariable("emitter_age", DoubleValue(age / 20.0))
        runtime.execute(effect.emitter.updateExpressions)


        when (effect.emitter.lifetime.getAction(runtime, started, age / 20.0)) {
            ParticleEmitterAction.GO -> {
                started = true
                val toEmit = effect.emitter.rate.getEmitCount(runtime, particles.size)
                repeat(times = toEmit) {
                    spawnParticle()
                }
            }
            ParticleEmitterAction.NOTHING -> {}
            ParticleEmitterAction.STOP -> stopped = true
            ParticleEmitterAction.RESET -> started = false
        }
    }

    fun spawnParticle() {
        runtime.environment.setSimpleVariable("particle_random_1", DoubleValue(Random.Default.nextDouble()))
        runtime.environment.setSimpleVariable("particle_random_2", DoubleValue(Random.Default.nextDouble()))
        runtime.environment.setSimpleVariable("particle_random_3", DoubleValue(Random.Default.nextDouble()))
        runtime.environment.setSimpleVariable("particle_random_4", DoubleValue(Random.Default.nextDouble()))

        val center = effect.emitter.shape.getCenter(runtime, entity).add(x, y, z)
        val newPosition = effect.emitter.shape.getNewParticlePosition(runtime, entity).add(x, y, z)
        val velocity = effect.particle.motion.getInitialVelocity(runtime, particlePos = newPosition, emitterPos = center).multiply(1/50.0)
        velocity.multiply(1 / 20.0)
        world.addParticle(particleEffect, newPosition.x, newPosition.y, newPosition.z, velocity.x, velocity.y, velocity.z)
    }
}
