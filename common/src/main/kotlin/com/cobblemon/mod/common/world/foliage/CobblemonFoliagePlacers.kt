package com.cobblemon.mod.common.world.foliage

import com.cobblemon.mod.common.platform.PlatformRegistry
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.world.gen.foliage.FoliagePlacerType

/**
 * Wow what a cool class
 *
 * @author Apion
 * @since May 23, 2024
 */
object CobblemonFoliagePlacers : PlatformRegistry<Registry<FoliagePlacerType<*>>, RegistryKey<Registry<FoliagePlacerType<*>>>, FoliagePlacerType<*>>() {
    val APRICORN_FOLIAGE_PLACER_TYPE = this.create("apricorn_foliage_placer", FoliagePlacerType(ApricornFoliagePlacer.CODEC.codec()))

    override val registry = Registries.FOLIAGE_PLACER_TYPE
    override val registryKey = RegistryKeys.FOLIAGE_PLACER_TYPE
}