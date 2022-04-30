package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.world.level.levelgen.feature.ApricornTreeFeature
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.util.registry.Registry
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.SingleStateFeatureConfig
import java.util.function.Supplier

object CobbledFeatures {
    private val featureRegistry = DeferredRegister.create(PokemonCobbled.MODID, Registry.FEATURE_KEY)

    private fun <T : Feature<*>> register(name: String, feature: Supplier<T>) : RegistrySupplier<T> {
        return featureRegistry.register(name, feature)
    }

    val APRICORN_TREE_FEATURE = register("apricorn_tree_feature") { ApricornTreeFeature(SingleStateFeatureConfig.CODEC) }

    fun register() {
        featureRegistry.register()
    }
}