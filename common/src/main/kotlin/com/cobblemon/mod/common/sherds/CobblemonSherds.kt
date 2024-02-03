package com.cobblemon.mod.common.sherds

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.block.DecoratedPotPatterns
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier

object CobblemonSherds {
    val allSherds = mutableListOf<CobblemonSherd>()

    val BYGONE_SHERD = addSherd(cobblemonResource("bygone_pottery_sherd"), CobblemonItems.BYGONE_SHERD)

    val CAPTURE_SHERD = addSherd(cobblemonResource("capture_pottery_sherd"), CobblemonItems.CAPTURE_SHERD)

    val DOME_SHERD = addSherd(cobblemonResource("dome_pottery_sherd"), CobblemonItems.DOME_SHERD)

    val HELIX_SHERD = addSherd(cobblemonResource("helix_pottery_sherd"), CobblemonItems.HELIX_SHERD)

    val NOSTALGIC_SHERD = addSherd(cobblemonResource("nostalgic_pottery_sherd"), CobblemonItems.NOSTALGIC_SHERD)

    fun addSherd(textureId: Identifier, item: Item): CobblemonSherd {
        val sherd = CobblemonSherd(textureId, item)
        allSherds.add(sherd)
        return sherd
    }
    fun registerSherds() {
        val registry = Registries.DECORATED_POT_PATTERN
        for (sherd in allSherds) {
            val regKey = RegistryKey.of(RegistryKeys.DECORATED_POT_PATTERN, sherd.textureId);
            Registry.register(
                registry,
                regKey,
                sherd.textureId.path
            )
            DecoratedPotPatterns.SHERD_TO_PATTERN = mutableMapOf()
        }

    }
}