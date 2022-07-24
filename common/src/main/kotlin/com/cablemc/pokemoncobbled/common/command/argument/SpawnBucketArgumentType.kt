package com.cablemc.pokemoncobbled.common.command.argument

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.spawning.SpawnBucket
import com.cablemc.pokemoncobbled.common.util.lang
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import java.util.concurrent.CompletableFuture
import net.minecraft.command.CommandSource

class SpawnBucketArgumentType: ArgumentType<SpawnBucket> {

    companion object {
        val EXAMPLES: List<String> = listOf(PokemonCobbled.bestSpawner.config.buckets.first().name)
        val INVALID_BUCKET = lang("command.checkspawns.invalid-bucket")

        fun spawnBucket() = SpawnBucketArgumentType()

        fun <S> getSpawnBucket(context: CommandContext<S>, name: String): SpawnBucket {
            return context.getArgument(name, SpawnBucket::class.java)
        }
    }

    override fun parse(reader: StringReader): SpawnBucket {
        val name = reader.readString()
        return PokemonCobbled.bestSpawner.config.buckets.find { it.name.equals(name, ignoreCase = true) }
            ?: throw SimpleCommandExceptionType(INVALID_BUCKET).createWithContext(reader)
    }

    override fun <S : Any> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        return CommandSource.suggestMatching(PokemonCobbled.bestSpawner.config.buckets.map { it.name }, builder)
    }

    override fun getExamples() = EXAMPLES
}