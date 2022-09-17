/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.command.argument

import com.cablemc.pokemoncobbled.common.api.moves.MoveTemplate
import com.cablemc.pokemoncobbled.common.api.moves.Moves
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import java.util.concurrent.CompletableFuture
import net.minecraft.command.CommandSource
import net.minecraft.text.MutableText
import net.minecraft.text.Text

class MoveArgumentType: ArgumentType<MoveTemplate> {

    override fun parse(reader: StringReader): MoveTemplate = Moves.getByName(reader.readString()) ?: throw SimpleCommandExceptionType(INVALID_MOVE).createWithContext(reader)

    override fun <S : Any> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        return CommandSource.suggestMatching(Moves.names(), builder)
    }

    override fun getExamples() = EXAMPLES

    companion object {

        val EXAMPLES: List<String> = listOf("tackle")
        val INVALID_MOVE: MutableText = Text.translatable("pokemoncobbled.command.pokespawn.invalid-move")

        fun move() = MoveArgumentType()

        fun <S> getMove(context: CommandContext<S>, name: String): MoveTemplate {
            return context.getArgument(name, MoveTemplate::class.java)
        }

    }

}