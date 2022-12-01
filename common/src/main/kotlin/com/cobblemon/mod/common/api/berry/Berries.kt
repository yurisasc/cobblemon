/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.berry

import com.cobblemon.mod.common.api.data.JsonDataRegistry
import com.cobblemon.mod.common.api.interaction.PokemonEntityInteraction
import com.cobblemon.mod.common.api.pokemon.status.Status
import com.cobblemon.mod.common.api.pokemon.status.Statuses
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.berry.CobblemonBerry
import com.cobblemon.mod.common.pokemon.interaction.HealStatusInteraction
import com.cobblemon.mod.common.util.adapters.CobblemonPokemonEntityInteractionTypeAdapter
import com.cobblemon.mod.common.util.adapters.FloatNumberRangeAdapter
import com.cobblemon.mod.common.util.adapters.StatusAdapter
import com.cobblemon.mod.common.util.cobblemonResource
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import net.minecraft.predicate.NumberRange
import net.minecraft.resource.ResourceType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

/**
 * The data registry for [Berry].
 *
 * @author Licious
 * @since November 28th, 2022
 */
object Berries : JsonDataRegistry<Berry> {

    override val id: Identifier = cobblemonResource("berries")
    override val type: ResourceType = ResourceType.SERVER_DATA
    override val observable = SimpleObservable<Berries>()

    override val gson = GsonBuilder()
        .disableHtmlEscaping()
        .setPrettyPrinting()
        .registerTypeAdapter(NumberRange.FloatRange::class.java, FloatNumberRangeAdapter)
        .registerTypeAdapter(PokemonEntityInteraction::class.java, CobblemonPokemonEntityInteractionTypeAdapter)
        .registerTypeAdapter(Status::class.java, StatusAdapter)
        .create()
    override val typeToken: TypeToken<Berry> = TypeToken.get(Berry::class.java)
    override val resourcePath = "berries"

    private val defaults = hashMapOf<Identifier, Berry>()
    private val custom = hashMapOf<Identifier, Berry>()

    val PECHA
        get() = this.byName("pecha")

    init {
        this.create("pecha", 2..4, 3..3, NumberRange.FloatRange.between(0.8, 1.0), 1..1, NumberRange.FloatRange.between(0.8, 1.0), 1..1,
            listOf(HealStatusInteraction(listOf(Statuses.POISON, Statuses.POISON_BADLY))),
            arrayOf(
                Triple(4.5, 10.5, 7.5),
                Triple(10.6, 12.4, 4.0),
                Triple(13.0, 14.4, 9.6),
                Triple(5.0, 16.4, 12.6),
                Triple(5.0, 23.7, 10.6),
                Triple(12.0, 24.7, 11.0),
                Triple(10.5, 20.7, 4.0),
                Triple(4.0, 20.7, 5.5)
            ),
            Flavor.SWEET to 10
        )
    }

    override fun reload(data: Map<Identifier, Berry>) {
        this.custom.clear()
        // ToDo once datapack berries are implemented load them here
    }

    // There's nothing to sync for clients atm
    override fun sync(player: ServerPlayerEntity) {}

    fun all() = this.defaults.filterKeys { !this.custom.containsKey(it) }.values + this.custom.values

    private fun create(
        name: String,
        baseYield: IntRange,
        lifeCycles: IntRange,
        temperatureRange: NumberRange.FloatRange,
        temperatureBonusYield: IntRange,
        downfallRange: NumberRange.FloatRange,
        downfallBonusYield: IntRange,
        interactions: Collection<PokemonEntityInteraction>,
        anchorPoints: Array<Triple<Double, Double, Double>>,
        vararg flavors: Pair<Flavor, Int>
    ) {
        val berry = CobblemonBerry(cobblemonResource(name), baseYield, lifeCycles, temperatureRange, temperatureBonusYield, downfallRange, downfallBonusYield, interactions, anchorPoints, flavors.toMap())
        this.defaults[berry.identifier] = berry
    }

    private fun byName(name: String): Berry {
        val identifier = cobblemonResource(name)
        return this.custom[identifier] ?: this.defaults[identifier]!!
    }

}