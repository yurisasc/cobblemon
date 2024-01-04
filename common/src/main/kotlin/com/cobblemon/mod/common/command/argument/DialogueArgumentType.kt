/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.command.argument

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.dialogue.Dialogues
import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import com.cobblemon.mod.common.util.asTranslated
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import java.util.concurrent.CompletableFuture
import net.minecraft.command.CommandSource
import net.minecraft.util.Identifier

class DialogueArgumentType : ArgumentType<Identifier> {

    companion object {
        val EXAMPLES: List<String> = listOf("cobblemon:example")
        val INVALID_DIALOGUE = "cobblemon.command.dialogue.invalid-dialogue".asTranslated()

        fun dialogue() = DialogueArgumentType()

        fun <S> getDialogue(context: CommandContext<S>, name: String): Identifier {
            return context.getArgument(name, Identifier::class.java)
        }
    }

    override fun parse(reader: StringReader): Identifier {
        try {
            return reader.asIdentifierDefaultingNamespace()
        } catch (e: Exception) {
            throw SimpleCommandExceptionType(INVALID_DIALOGUE).createWithContext(reader)
        }
    }

    override fun <S : Any> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        return CommandSource.suggestMatching(Dialogues.dialogues.keys.map { if (it.namespace == Cobblemon.MODID) it.path else it.toString() }, builder)
    }

    override fun getExamples() = EXAMPLES
}