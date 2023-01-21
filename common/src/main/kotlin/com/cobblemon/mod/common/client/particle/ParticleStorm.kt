package com.cobblemon.mod.common.client.particle

import com.bedrockk.molang.runtime.MoLangRuntime
import com.bedrockk.molang.runtime.value.DoubleValue
import com.cobblemon.mod.common.api.snowstorm.BedrockParticleEffect
import com.cobblemon.mod.common.client.render.SnowstormParticle
import kotlin.random.Random
import net.minecraft.client.MinecraftClient
import net.minecraft.client.particle.NoRenderParticle
import net.minecraft.client.particle.Particle
import net.minecraft.client.world.ClientWorld
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes

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
    val runtime = MoLangRuntime()
    val particles = mutableListOf<SnowstormParticle>()
    var started = false
    var stormAge = 0F

    override fun tick() {
        super.tick()
        val pos = origin.position
        x = pos.x
        y = pos.y
        z = pos.z

        val delta = MinecraftClient.getInstance().tickDelta / 20
        runtime.environment.setValue("variable.emitter_random_1", DoubleValue(Random.Default.nextDouble()))
        runtime.environment.setValue("variable.emitter_random_2", DoubleValue(Random.Default.nextDouble()))
        runtime.environment.setValue("variable.emitter_random_3", DoubleValue(Random.Default.nextDouble()))
        runtime.environment.setValue("variable.emitter_random_4", DoubleValue(Random.Default.nextDouble()))
        stormAge += delta
        val toEmit = effect.emitter.rate.getEmitCount(runtime, particles.size, delta)
        repeat(times = toEmit) {
            spawnParticle()
        }
    }

    fun spawnParticle() {
        val center = effect.emitter.shape.getCenter(runtime)
        val newPosition = effect.emitter.shape.getNewParticlePosition(runtime).add(x, y, z)
        val velocity = effect.particle.motion.getInitialVelocity(runtime, newPosition, center)
        val particle = SnowstormParticle(this, world, x, y, z, velocity)
//        world.addParticle()
//        ParticleTypes
        MinecraftClient.getInstance().particleManager.addParticle(particle)
        particles.add(particle)
    }
}
