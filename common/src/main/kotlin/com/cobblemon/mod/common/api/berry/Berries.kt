/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.berry

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.data.JsonDataRegistry
import com.cobblemon.mod.common.api.interaction.PokemonEntityInteraction
import com.cobblemon.mod.common.api.pokemon.status.Status
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.util.adapters.*
import com.cobblemon.mod.common.util.cobblemonResource
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import net.minecraft.predicate.NumberRange
import net.minecraft.resource.ResourceType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import java.awt.Color

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
        .registerTypeAdapter(Box::class.java, BoxAdapter)
        .registerTypeAdapter(Vec3d::class.java, VerboseVec3dAdapter)
        .registerTypeAdapter(Identifier::class.java, IdentifierAdapter)
        .registerTypeAdapter(GrowthFactor::class.java, CobblemonGrowthFactorAdapter)
        .registerTypeAdapter(IntRange::class.java, VerboseIntRangeAdapter)
        .registerTypeAdapter(Color::class.java, LiteralHexColorAdapter)
        .create()
    override val typeToken: TypeToken<Berry> = TypeToken.get(Berry::class.java)
    override val resourcePath = "berries"

    private val berries = hashMapOf<Identifier, Berry>()

    val PECHA
        get() = this.getByName("pecha")

    override fun reload(data: Map<Identifier, Berry>) {
        this.berries.clear()
        data.forEach { (identifier, berry) ->
            try {
                berry.identifier = identifier
                berry.validate()
                this.berries[identifier] = berry
            } catch (e: Exception) {
                Cobblemon.LOGGER.error("Skipped loading the {} berry", identifier, e)
            }
        }
        this.observable.emit(this)
    }

    override fun sync(player: ServerPlayerEntity) {
        // ToDo sync clients
    }

    fun all() = this.berries.values.toList()

    /**
     * Gets a berry if loaded.
     *
     * @param identifier The identifier of the berry.
     * @return The [Berry] if loaded otherwise null.
     */
    fun getByIdentifier(identifier: Identifier): Berry? = this.berries[identifier]

    /**
     * Gets a berry if loaded.
     *
     * @param name The path of the identifier of the berry under the [Cobblemon.MODID] namespace.
     * @return The [Berry] if loaded otherwise null.
     */
    fun getByName(name: String): Berry? = this.getByIdentifier(cobblemonResource(name))

}