package com.cablemc.pokemoncobbled.common.command.argument

import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.pokemon.Species
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.network.chat.TranslatableComponent
import java.util.concurrent.CompletableFuture

//Very helpful for all command related stuff: https://fabricmc.net/wiki/tutorial:commands#brigadier_explained
class PokemonArgumentType: ArgumentType<Species> {

    companion object {
        val EXAMPLES: List<String> = listOf("eevee")
        val INVALID_POKEMON = TranslatableComponent("pokemoncobbled.command.pokespawn.invalid-pokemon")

        fun pokemon() = PokemonArgumentType()

        fun <S> getPokemon(context: CommandContext<S>, name: String): Species {
            return context.getArgument(name, Species::class.java)
        }
    }

    override fun parse(reader: StringReader): Species {

        val pkmString = reader.readString()

        return PokemonSpecies.getByName(pkmString.lowercase())
            ?: throw SimpleCommandExceptionType(INVALID_POKEMON).createWithContext(
                reader
            )
    }

    override fun <S : Any> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        return SharedSuggestionProvider.suggest(PokemonSpecies.species.map { it.name }, builder)
    }

    override fun getExamples() = EXAMPLES
}