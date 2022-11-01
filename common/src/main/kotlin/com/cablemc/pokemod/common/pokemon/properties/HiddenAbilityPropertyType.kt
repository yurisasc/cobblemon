package com.cablemc.pokemod.common.pokemon.properties

import com.cablemc.pokemod.common.api.properties.CustomPokemonProperty
import com.cablemc.pokemod.common.api.properties.CustomPokemonPropertyType
import com.cablemc.pokemod.common.pokemon.Pokemon
import com.cablemc.pokemod.common.pokemon.abilities.HiddenAbilityType

/**
 * A type of [CustomPokemonPropertyType] which asserts that the Pokémon's ability
 * must be a hidden ability.
 *
 * @author Hiroku
 * @since November 1st, 2022
 */
object HiddenAbilityPropertyType : CustomPokemonPropertyType<HiddenAbilityProperty> {
    override val keys = setOf("hiddenability", "ha")
    override val needsKey = true
    override fun fromString(value: String?) = HiddenAbilityProperty()
    override fun examples() = emptySet<String>()
}

class HiddenAbilityProperty : CustomPokemonProperty {
    override fun asString() = "hiddenability"
    override fun apply(pokemon: Pokemon) {
        val hiddenAbilities = pokemon.form.abilities.mapping.flatMap { it.value }.filter { it.type == HiddenAbilityType }
        if (hiddenAbilities.isNotEmpty()) {
            pokemon.ability = hiddenAbilities.random().template.create()
        }
    }

    override fun matches(pokemon: Pokemon) = pokemon.form.abilities.mapping
        .flatMap { it.value }
        .find { it.template == pokemon.ability.template }
        ?.type == HiddenAbilityType
}