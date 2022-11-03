/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.abilities

import com.cablemc.pokemod.common.Pokemod
import com.cablemc.pokemod.common.api.data.JsonDataRegistry
import com.cablemc.pokemod.common.api.reactive.SimpleObservable
import com.cablemc.pokemod.common.net.messages.client.data.AbilityRegistrySyncPacket
import com.cablemc.pokemod.common.pokemon.abilities.HiddenAbilityType
import com.cablemc.pokemod.common.util.asTranslated
import com.cablemc.pokemod.common.util.pokemodResource
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import kotlin.io.path.Path
import net.minecraft.resource.ResourceType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.MutableText
import net.minecraft.util.Identifier

/**
 * Registry for all known Abilities
 */
object Abilities : JsonDataRegistry<AbilityTemplate> {
    override val id = pokemodResource("abilities")
    override val type = ResourceType.SERVER_DATA
    override val observable = SimpleObservable<Abilities>()
    override val typeToken: TypeToken<AbilityTemplate> = TypeToken.get(AbilityTemplate::class.java)
    override val resourcePath = Path("abilities")
    override val gson: Gson = GsonBuilder()
        .disableHtmlEscaping()
        .setLenient()
        .setPrettyPrinting()
        .registerTypeAdapter(MutableText::class.java, MutableTextAdapter)
        .create()

    val DUMMY = AbilityTemplate(name = "dummy")

    private val allAbilities = mutableListOf<AbilityTemplate>()
    private val abilityMap = mutableMapOf<String, AbilityTemplate>()

    object MutableTextAdapter : JsonDeserializer<MutableText> {
        override fun deserialize(json: JsonElement, type: Type, ctx: JsonDeserializationContext) = json.asString.asTranslated()
    }

    override fun reload(data: Map<Identifier, AbilityTemplate>) {
        PotentialAbility.types.clear()
        PotentialAbility.types.add(CommonAbilityType)
        PotentialAbility.types.add(HiddenAbilityType)

        allAbilities.clear()
        abilityMap.clear()

        data.forEach { (identifier, abilityTemplate) ->
            allAbilities.add(abilityTemplate)
            abilityMap[identifier.path] = abilityTemplate
        }

        Pokemod.LOGGER.info("Loaded {} abilities", this.allAbilities.size)
        this.observable.emit(this)
    }

    override fun sync(player: ServerPlayerEntity) {
        AbilityRegistrySyncPacket().sendToPlayer(player)
    }

    fun register(ability: AbilityTemplate): AbilityTemplate {
        abilityMap[ability.name.lowercase()] = ability
        allAbilities.add(ability)
        return ability
    }

    fun all() = allAbilities.toList()
    fun first() = allAbilities.first()
    fun get(name: String) = abilityMap[name.lowercase()]
    fun getOrException(name: String) = get(name)!!
    fun count() = allAbilities.size
}