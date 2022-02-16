package com.cablemc.pokemoncobbled.forge.common.command.provider

import com.cablemc.pokemoncobbled.forge.common.api.pokemon.PokemonSpecies
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.SharedSuggestionProvider
import java.util.concurrent.CompletableFuture

class PokemonNameSuggestions : SuggestionProvider<CommandSourceStack> {

    override fun getSuggestions(
        context: CommandContext<CommandSourceStack>?,
        builder: SuggestionsBuilder?
    ): CompletableFuture<Suggestions> {
        return SharedSuggestionProvider.suggest(PokemonSpecies.species.map { it.name }.toList(), builder)
    }

}