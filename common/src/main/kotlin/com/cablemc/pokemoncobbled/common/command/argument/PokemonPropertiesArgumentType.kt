/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.command.argument

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
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

    override fun <S : Any> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        // Just a neat shortcut for our own Pokémon since that will be the most common setup no need to overcomplicate
        return CommandSource.suggestMatching(PokemonSpecies.species.map { if (it.resourceIdentifier.namespace == PokemonCobbled.MODID) it.resourceIdentifier.path else it.resourceIdentifier.toString() }, builder)
    }

    override fun getExamples() = EXAMPLES
}