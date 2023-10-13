package com.cobblemon.mod.common.api.pokemon.evolution.adapters

import com.cobblemon.mod.common.api.pokemon.evolution.Evolution
import com.cobblemon.mod.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cobblemon.mod.common.util.cobblemonResource
import com.mojang.serialization.Codec
import com.mojang.serialization.Lifecycle
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.SimpleRegistry

/**
 *
 */
object EvolutionRegistry {

    private val EVOLUTION_REGISTRY = SimpleRegistry<Variant<Evolution>>(RegistryKey.ofRegistry(cobblemonResource("evolution_variants")), Lifecycle.stable())
    private val REQUIREMENT_REGISTRY = SimpleRegistry<Variant<EvolutionRequirement>>(RegistryKey.ofRegistry(cobblemonResource("evolution_requirements")), Lifecycle.stable())

    /**
     *
     */
    @JvmField
    val EVOLUTION_CODEC: Codec<Evolution> = EVOLUTION_REGISTRY.codec.dispatch("variant", Evolution::variant) { variant -> variant.codec }

    /**
     *
     */
    @JvmField
    val REQUIREMENT_CODEC: Codec<EvolutionRequirement> = REQUIREMENT_REGISTRY.codec.dispatch("variant", EvolutionRequirement::variant) { variant -> variant.codec }

    /**
     * TODO
     *
     * @param variant The [Variant] of an [Evolution] being registered.
     *
     * @throws IllegalStateException If the backing registry already contains an entry with the [Variant.identifier].
     * @throws AssertionError If the registry is missing an intrusive holder.
     */
    @JvmStatic
    fun registerVariant(variant: Variant<Evolution>) {
        EVOLUTION_REGISTRY.add(RegistryKey.of(EVOLUTION_REGISTRY.key, variant.identifier), variant, Lifecycle.stable())
    }

    /**
     * TODO
     *
     * @param variant The [Variant] of an [EvolutionRequirement] being registered.
     *
     * @throws IllegalStateException If the backing registry already contains an entry with the [Variant.identifier].
     * @throws AssertionError If the registry is missing an intrusive holder.
     */
    @JvmStatic
    fun registerRequirement(variant: Variant<EvolutionRequirement>) {
        REQUIREMENT_REGISTRY.add(RegistryKey.of(REQUIREMENT_REGISTRY.key, variant.identifier), variant, Lifecycle.stable())
    }

}