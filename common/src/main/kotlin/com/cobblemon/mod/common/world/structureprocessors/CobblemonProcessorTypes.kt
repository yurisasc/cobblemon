package com.cobblemon.mod.common.world.structureprocessors

import com.cobblemon.mod.common.platform.PlatformRegistry
import com.cobblemon.mod.common.util.cobblemonResource
import com.mojang.serialization.Codec
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.structure.processor.StructureProcessor
import net.minecraft.structure.processor.StructureProcessorType
import net.minecraft.world.gen.placementmodifier.PlacementModifier
import net.minecraft.world.gen.placementmodifier.PlacementModifierType

object CobblemonProcessorTypes {
    val registry = Registries.STRUCTURE_PROCESSOR

    val PROBABILITY_PROCESSOR = register("probability", ProbabilityProcessor.CODEC)

    fun <T : StructureProcessor> register(id: String, codec: Codec<T>): StructureProcessorType<T> {
        return Registry.register(registry, cobblemonResource(id), StructureProcessorType { codec })
    }

    fun touch() = Unit
}
