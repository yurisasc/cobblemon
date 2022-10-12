/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.command.argument

import com.cablemc.pokemod.common.Pokemod
import com.cablemc.pokemod.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemod.common.pokemon.Species
import com.cablemc.pokemod.common.util.asIdentifierDefaultingNamespace
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import java.util.concurrent.CompletableFuture
import net.minecraft.command.CommandSource
import net.minecraft.text.Text

//Very helpful for all command related stuff: https://fabricmc.net/wiki/tutorial:commands#brigadier_explained
class PokemonArgumentType : ArgumentType<Species> {

    companion object {
        val EXAMPLES: List<String> = listOf("eevee")
        val INVALID_POKEMON = Text.translatable("pokemod.command.pokespawn.invalid-pokemon")

        fun pokemon() = PokemonArgumentType()

        fun <S> getPokemon(context: CommandContext<S>, name: String): Species {
            return context.getArgument(name, Species::class.java)
        }
    }

    override fun parse(reader: StringReader): Species {
        return PokemonSpecies.getByIdentifier(reader.asIdentifierDefaultingNamespace())
            ?: throw SimpleCommandExceptionType(INVALID_POKEMON).createWithContext(reader)
    }

    override fun <S : Any> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        // Just a neat shortcut for our own Pokémon since that will be the most common setup no need to overcomplicate
        return CommandSource.suggestMatching(PokemonSpecies.species.map { if (it.resourceIdentifier.namespace == Pokemod.MODID) it.resourceIdentifier.path else it.resourceIdentifier.toString() }, builder)
    }

    override fun getExamples() = EXAMPLES
}