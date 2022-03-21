package com.cablemc.pokemoncobbled.common.pokemon

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.abilities.AbilityTemplate
import com.cablemc.pokemoncobbled.common.api.abilities.adapters.AbilityTemplateAdapter
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.cablemc.pokemoncobbled.common.api.pokemon.effect.ShoulderEffect
import com.cablemc.pokemoncobbled.common.api.pokemon.effect.adapter.ShoulderEffectAdapter
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.Evolution
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cablemc.pokemoncobbled.common.api.types.ElementalType
import com.cablemc.pokemoncobbled.common.api.types.adapters.ElementalTypeAdapter
import com.cablemc.pokemoncobbled.common.pokemon.adapters.StatAdapter
import com.cablemc.pokemoncobbled.common.pokemon.evolution.adapters.CobbledEvolutionAdapter
import com.cablemc.pokemoncobbled.common.pokemon.evolution.adapters.CobbledRequirementAdapter
import com.cablemc.pokemoncobbled.common.pokemon.stats.Stat
import com.cablemc.pokemoncobbled.common.util.adapters.IntRangeAdapter
import com.cablemc.pokemoncobbled.common.util.adapters.ItemStackAdapter
import com.cablemc.pokemoncobbled.common.util.adapters.ResourceLocationAdapter
import com.cablemc.pokemoncobbled.common.util.adapters.pokemonPropertiesShortAdapter
import com.cablemc.pokemoncobbled.common.util.fromJson
import com.google.gson.GsonBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import java.io.InputStreamReader

object SpeciesLoader {

    val GSON = GsonBuilder()
        .registerTypeAdapter(Stat::class.java, StatAdapter)
        .registerTypeAdapter(ElementalType::class.java, ElementalTypeAdapter)
        .registerTypeAdapter(AbilityTemplate::class.java, AbilityTemplateAdapter)
        .registerTypeAdapter(ShoulderEffect::class.java, ShoulderEffectAdapter)
        .registerTypeAdapter(Evolution::class.java, CobbledEvolutionAdapter)
        .registerTypeAdapter(EvolutionRequirement::class.java, CobbledRequirementAdapter)
        .registerTypeAdapter(IntRange::class.java, IntRangeAdapter)
        .registerTypeAdapter(ItemStack::class.java, ItemStackAdapter)
        .registerTypeAdapter(PokemonProperties::class.java, pokemonPropertiesShortAdapter)
        .registerTypeAdapter(ResourceLocation::class.java, ResourceLocationAdapter)
        .disableHtmlEscaping()
        .create()

    fun loadFromAssets(name: String): Species {
        // TODO add proper error handling
        val inputStream = PokemonCobbled::class.java.getResourceAsStream("/assets/${PokemonCobbled.MODID}/species/$name.json")!!
        return GSON.fromJson<Species>(InputStreamReader(inputStream))
    }

}