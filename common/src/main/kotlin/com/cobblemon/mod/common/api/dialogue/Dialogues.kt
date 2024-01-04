/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.dialogue

import com.bedrockk.molang.Expression
import com.cobblemon.mod.common.api.data.JsonDataRegistry
import com.cobblemon.mod.common.api.dialogue.input.DialogueInput
import com.cobblemon.mod.common.api.molang.ExpressionLike
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.util.adapters.DialogueActionAdapter
import com.cobblemon.mod.common.util.adapters.DialogueFaceProviderAdapter
import com.cobblemon.mod.common.util.adapters.DialogueInputAdapter
import com.cobblemon.mod.common.util.adapters.DialoguePredicateAdapter
import com.cobblemon.mod.common.util.adapters.DialogueTextAdapter
import com.cobblemon.mod.common.util.adapters.ExpressionAdapter
import com.cobblemon.mod.common.util.adapters.ExpressionLikeAdapter
import com.cobblemon.mod.common.util.adapters.IdentifierAdapter
import com.cobblemon.mod.common.util.adapters.TextAdapter
import com.cobblemon.mod.common.util.cobblemonResource
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import net.minecraft.resource.ResourceType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Identifier

/**
 * Registry for dialogue data.
 *
 * @see Dialogue
 * @since December 29th, 2023
 * @author Hiroku
 */
object Dialogues : JsonDataRegistry<Dialogue> {
    override val id = cobblemonResource("dialogues")
    override val type = ResourceType.SERVER_DATA
    override val observable = SimpleObservable<Dialogues>()

    val dialogues = mutableMapOf<Identifier, Dialogue>()
    /** If you need custom adapters registered, subscribe to this and register them. */
    val gsonObservable: SimpleObservable<GsonBuilder> = SimpleObservable()

    override fun sync(player: ServerPlayerEntity) {}

    override val gson = GsonBuilder()
        .registerTypeAdapter(DialogueAction::class.java, DialogueActionAdapter)
        .registerTypeAdapter(DialoguePredicate::class.java, DialoguePredicateAdapter)
        .registerTypeAdapter(DialogueInput::class.java, DialogueInputAdapter)
        .registerTypeAdapter(DialogueFaceProvider::class.java, DialogueFaceProviderAdapter)
        .registerTypeAdapter(DialogueText::class.java, DialogueTextAdapter)
        .registerTypeAdapter(Expression::class.java, ExpressionAdapter)
        .registerTypeAdapter(ExpressionLike::class.java, ExpressionLikeAdapter)
        .registerTypeAdapter(MutableText::class.java, TextAdapter)
        .registerTypeAdapter(Identifier::class.java, IdentifierAdapter)
        .also { gsonObservable.emit(it) }
        .create()

    override val typeToken = TypeToken.get(Dialogue::class.java)
    override val resourcePath = "dialogues"

    override fun reload(data: Map<Identifier, Dialogue>) {
        dialogues.putAll(data)
        observable.emit(this)
    }
}