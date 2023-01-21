package com.cobblemon.mod.common.particle

import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.registry.CompletableRegistry
import dev.architectury.registry.registries.RegistrySupplier
import java.util.function.Supplier
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.particle.ParticleType
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.registry.Registry

object CobblemonParticles : CompletableRegistry<ParticleType<*>>(Registry.PARTICLE_TYPE_KEY) {
    private fun <T : ParticleType<*>> register(name: String, particleType: Supplier<T>): RegistrySupplier<T> {
        return queue(name, particleType)
    }

    val SNOWSTORM_PARTICLE_TYPE = register("snowstorm") { SnowstormParticleType() }
}