package com.cobblemon.mod.common.api.types

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.registry.CobblemonRegistries
import com.google.gson.JsonParser
import com.mojang.serialization.JsonOps
import net.minecraft.resources.FileToIdConverter
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.util.ColorRGBA

object ElementalTypeDisplays {

    private val assets = hashMapOf<ResourceLocation, ElementalTypeDisplay>()

    private val dummy = ElementalTypeDisplay(ColorRGBA(0))

    private val path = "${CobblemonRegistries.ELEMENTAL_TYPE_KEY.location().namespace}/${CobblemonRegistries.ELEMENTAL_TYPE_KEY.location().path}"

    fun reload(resourceManager: ResourceManager) {
        this.assets.clear()
        val fileToIdConverter = FileToIdConverter.json(this.path)
        fileToIdConverter.listMatchingResources(resourceManager).forEach { (fileId, resource) ->
            resource.openAsReader().use { reader ->
                val jElement = JsonParser.parseReader(reader)
                val id = fileToIdConverter.fileToId(fileId)
                ElementalTypeDisplay.CODEC.decode(JsonOps.INSTANCE, jElement)
                    .ifSuccess { this.assets[id] = it.first }
                    .ifError { Cobblemon.LOGGER.error("Failed to load elemental type display {}: {}", id, it.message()) }
            }
        }
        Cobblemon.LOGGER.info("Loaded {} elemental type displays", this.assets.size)
    }


    fun displayOf(type: ElementalType): ElementalTypeDisplay = this.assets[type.resourceLocation()] ?: this.dummy

}