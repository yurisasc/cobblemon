package com.cablemc.pokemoncobbled.common.pokemon.evolution.adapters

import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.adapters.RequirementAdapter
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements.AreaRequirement
import com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements.BiomeRequirement
import com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements.FriendshipRequirement
import com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements.LevelRequirement
import com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements.MoveSetRequirement
import com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements.MoveTypeRequirement
import com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements.PartyMemberRequirement
import com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements.PokemonPropertiesRequirement
import com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements.TimeRangeRequirement
import com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements.WeatherRequirement
import com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements.WorldRequirement
import com.google.common.collect.HashBiMap
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import java.lang.reflect.Type
import kotlin.reflect.KClass

/**
 * The default implementation of [RequirementAdapter].
 *
 * @author Licious
 * @since March 21st, 2022
 */
object CobbledRequirementAdapter : RequirementAdapter {

    private const val VARIANT = "variant"

    private val types = HashBiMap.create<String, KClass<out EvolutionRequirement>>()

    init {
        this.registerType(AreaRequirement.ADAPTER_VARIANT, AreaRequirement::class)
        this.registerType(BiomeRequirement.ADAPTER_VARIANT, BiomeRequirement::class)
        this.registerType(FriendshipRequirement.ADAPTER_VARIANT, FriendshipRequirement::class)
        // ToDo Pending impl of held items
        //this.registerType(HeldItemRequirement.ADAPTER_VARIANT, HeldItemRequirement::class)
        this.registerType(WorldRequirement.ADAPTER_VARIANT, WorldRequirement::class)
        this.registerType(MoveSetRequirement.ADAPTER_VARIANT, MoveSetRequirement::class)
        this.registerType(MoveTypeRequirement.ADAPTER_VARIANT, MoveTypeRequirement::class)
        this.registerType(PartyMemberRequirement.ADAPTER_VARIANT, PartyMemberRequirement::class)
        this.registerType(PokemonPropertiesRequirement.ADAPTER_VARIANT, PokemonPropertiesRequirement::class)
        this.registerType(TimeRangeRequirement.ADAPTER_VARIANT, TimeRangeRequirement::class)
        this.registerType(LevelRequirement.ADAPTER_VARIANT, LevelRequirement::class)
        this.registerType(WeatherRequirement.ADAPTER_VARIANT, WeatherRequirement::class)
    }

    override fun <T : EvolutionRequirement> registerType(id: String, type: KClass<T>) {
        this.types[id.lowercase()] = type
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): EvolutionRequirement {
        val variant = json.asJsonObject.get(VARIANT).asString.lowercase()
        val type = this.types[variant] ?: throw IllegalArgumentException("Cannot resolve type for variant $variant")
        return context.deserialize(json, type.java)
    }

    override fun serialize(src: EvolutionRequirement, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val json = context.serialize(src, src::class.java).asJsonObject
        val variant = this.types.inverse()[src::class] ?: throw IllegalArgumentException("Cannot resolve variant for type ${src::class.qualifiedName}")
        json.addProperty(VARIANT, variant)
        return json
    }

}