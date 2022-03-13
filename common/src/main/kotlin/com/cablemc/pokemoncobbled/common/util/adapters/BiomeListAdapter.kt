package com.cablemc.pokemoncobbled.common.util.adapters

import com.cablemc.pokemoncobbled.common.PokemonCobbled.LOGGER
import com.cablemc.pokemoncobbled.common.api.spawning.BiomeList
import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import net.minecraft.core.Registry.BIOME_REGISTRY
import net.minecraft.core.RegistryAccess
import net.minecraft.resources.ResourceLocation
import java.lang.reflect.Type

/**
 * A cheeky workaround to the need for spawning to be able to arbitrarily load either a biome or a configured
 * biome category.
 *
 * @author Hiroku
 * @since January 24th, 2022
 */
object BiomeListAdapter : JsonSerializer<BiomeList>, JsonDeserializer<BiomeList> {
    override fun serialize(list: BiomeList, type: Type, ctx: JsonSerializationContext): JsonElement {
        val json = JsonArray()
        list.forEach { json.add(it.toString()) }
        return json
    }

    override fun deserialize(json: JsonElement, type: Type, ctx: JsonDeserializationContext): BiomeList {
        val list = BiomeList()
        json.asJsonArray.forEach { element ->
            val biomeName = ResourceLocation(element.asString)

            // TODO also try retrieving with the element as a biome category
            // Maybe rework the biome list object to preserve what were categories

            val biomeRegistry = RegistryAccess.BUILTIN.get().registryOrThrow(BIOME_REGISTRY)
            val biome = biomeRegistry.entrySet().find { it.key.location() == biomeName }?.key
            if (biome == null) {
                LOGGER.warn("Unrecognized biome: $biomeName")
            } else {
                list.add(biomeName)
            }
        }
        return list
    }
}