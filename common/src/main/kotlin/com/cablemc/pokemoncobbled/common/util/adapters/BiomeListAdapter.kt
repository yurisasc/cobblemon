package com.cablemc.pokemoncobbled.common.util.adapters

import com.cablemc.pokemoncobbled.common.PokemonCobbled.LOGGER
import com.cablemc.pokemoncobbled.common.api.spawning.BiomeList
import com.google.gson.*
import net.minecraft.util.Identifier
import net.minecraft.util.registry.DynamicRegistryManager
import net.minecraft.util.registry.Registry
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
            val biomeName = Identifier(element.asString)

            // TODO also try retrieving with the element as a biome category
            // Maybe rework the biome list object to preserve what were categories
            val biomeRegistry = DynamicRegistryManager.BUILTIN.get().get(Registry.BIOME_KEY)
            val biome = biomeRegistry.entrySet.find { it.key.value == biomeName }?.key
            if (biome == null) {
                LOGGER.warn("Unrecognized biome: $biomeName")
            } else {
                list.add(biomeName)
            }
        }
        return list
    }
}