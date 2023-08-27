/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.util.adapters

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.interaction.EntityInteractionTypeAdapter
import com.cobblemon.mod.common.api.interaction.PokemonEntityInteraction
import com.cobblemon.mod.common.pokemon.interaction.*
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import net.minecraft.util.Identifier
import java.lang.reflect.Type
import kotlin.reflect.KClass

object CobblemonPokemonEntityInteractionTypeAdapter : EntityInteractionTypeAdapter<PokemonEntityInteraction> {

    private const val VARIANT = "variant"
    private val types = hashMapOf<String, KClass<out PokemonEntityInteraction>>()

    init {
        registerInteraction(EvMutationInteraction.TYPE_ID, EvMutationInteraction::class)
        registerInteraction(FriendshipIncrementInteraction.TYPE_ID, FriendshipIncrementInteraction::class)
        registerInteraction(HealStatusInteraction.TYPE_ID, HealStatusInteraction::class)
        registerInteraction(HpRestoreInteraction.TYPE_ID, HpRestoreInteraction::class)
        registerInteraction(PpRestoreInteraction.TYPE_ID, PpRestoreInteraction::class)
    }

    override fun registerInteraction(identifier: Identifier, type: KClass<out PokemonEntityInteraction>) {
        val existing = types.put(identifier.toString(), type)
        if (existing != null) {
            Cobblemon.LOGGER.debug("Replaced {} under ID {} with {} in the {}", existing::class.qualifiedName, identifier.toString(), type.qualifiedName, this::class.qualifiedName)
        }
    }

    override fun deserialize(jsonIn: JsonElement, type: Type, context: JsonDeserializationContext): PokemonEntityInteraction {
        val json = jsonIn.asJsonObject
        val variant = json.get(VARIANT).asString.lowercase()
        val registeredType = types[variant] ?: throw IllegalArgumentException("Cannot resolve type for variant $variant")
        return context.deserialize(json, registeredType.java)
    }

    override fun serialize(interaction: PokemonEntityInteraction, type: Type, context: JsonSerializationContext): JsonElement {
        val json = context.serialize(interaction).asJsonObject
        val variant = types.entries.find { it.value == interaction::class }?.key ?: throw IllegalArgumentException("Cannot resolve variant for type ${interaction::class.qualifiedName}")
        json.addProperty(VARIANT, variant)
        return json
    }
}