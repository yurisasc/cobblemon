package com.cablemc.pokemoncobbled.common.util.adapters

import com.cablemc.pokemoncobbled.common.api.spawning.BiomeList
import com.cablemc.pokemoncobbled.mod.PokemonCobbledMod
import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.biome.Biome
import net.minecraftforge.fmllegacy.common.registry.GameRegistry
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
        list.forEach { json.add(it.registryName.toString()) }
        return json
    }

    override fun deserialize(json: JsonElement, type: Type, ctx: JsonDeserializationContext): BiomeList {
        val list = BiomeList()
        json.asJsonArray.forEach { element ->
            val biomeName = ResourceLocation(element.asString)
            val biome = GameRegistry.findRegistry(Biome::class.java).entries.find { it.key.registryName == biomeName }?.value
            if (biome == null) {
                PokemonCobbledMod.LOGGER.warn("Unrecognized biome: $biomeName")
            } else {
                list.add(biome)
            }
        }
        return list
    }
}