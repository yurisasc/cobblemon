/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.command.argument

import com.cablemc.pokemod.common.Pokemod
import com.cablemc.pokemod.common.api.pokemon.PokemonProperties
import com.cablemc.pokemod.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemod.common.api.properties.CustomPokemonProperty
import com.cablemc.pokemod.common.api.properties.CustomPokemonPropertyType
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.command.CommandSource
import java.util.concurrent.CompletableFuture
class PokemonPropertiesArgumentType: ArgumentType<PokemonProperties> {

    companion object {
        val EXAMPLES: List<String> = listOf("eevee")

        fun properties() = PokemonPropertiesArgumentType()

        fun <S> getPokemonProperties(context: CommandContext<S>, name: String): PokemonProperties {
            return context.getArgument(name, PokemonProperties::class.java)
        }
    }

    override fun parse(reader: StringReader): PokemonProperties {
        val properties = reader.remaining
        reader.cursor = reader.totalLength

        return PokemonProperties.parse(properties)
    }

    override fun <S : Any?> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        val sections = builder.remainingLowerCase.split(" ")
        if (sections.isEmpty())
            return this.suggestSpeciesAndPropertyKeys(builder)
        val currentSection = sections.last()
        /**
         * We already have a property defined and a potential value, let's try to suggest values based on provided [CustomPokemonPropertyType.examples]
         */
        if (currentSection.contains("=")) {
            val propertyKey = currentSection.substringBefore("=")
            val currentValue = currentSection.substringAfter("=")
            val property = CustomPokemonProperty.properties.firstOrNull { property -> property.keys.any { key -> key.equals(propertyKey, true) } } ?: return Suggestions.empty()
            return this.suggestPropertyValue(builder, property, currentValue)
        }
        // We will always assume a species identifier has the priority as the first command argument as that's the most traditional approach as such lets provide property keys for the suggestion
        else if (sections.size >= 2) {
            this.collectPropertyKeys().forEach { key ->

            }
        }
        return this.suggestSpeciesAndPropertyKeys(builder)
    }

    private fun suggestSpeciesAndPropertyKeys(builder: SuggestionsBuilder) = CommandSource.suggestMatching(this.collectSpeciesIdentifiers() + this.collectPropertyKeys(), builder)

    private fun collectSpeciesIdentifiers() = PokemonSpecies.species.map { if (it.resourceIdentifier.namespace == Pokemod.MODID) it.resourceIdentifier.path else it.resourceIdentifier.toString() }

    private fun collectPropertyKeys() = CustomPokemonProperty.properties.mapNotNull { it.keys.firstOrNull()?.lowercase() }

    private fun suggestPropertyValue(builder: SuggestionsBuilder, property: CustomPokemonPropertyType<*>, currentValue: String): CompletableFuture<Suggestions> {
        property.examples().forEach { example ->
            val substring = example.substringAfter(currentValue)
            if (substring.isNotBlank()) {
                builder.suggest(substring)
            }
        }
        return builder.buildFuture()
    }

    override fun getExamples() = EXAMPLES
}