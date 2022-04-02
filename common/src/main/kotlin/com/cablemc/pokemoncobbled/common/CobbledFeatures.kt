package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.world.level.levelgen.feature.ApricornTreeFeature
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.core.Registry
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration
import java.util.function.Supplier

object CobbledFeatures {
    private val featureRegistry = DeferredRegister.create(PokemonCobbled.MODID, Registry.FEATURE_REGISTRY)

    private fun <T : Feature<*>> register(name: String, feature: Supplier<T>) : RegistrySupplier<T> {
        return featureRegistry.register(name, feature)
    }

    val APRICORN_TREE_FEATURE = register("apricorn_tree_feature") { ApricornTreeFeature(BlockStateConfiguration.CODEC) }

    fun register() {
        featureRegistry.register()
    }
}