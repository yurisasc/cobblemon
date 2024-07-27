/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.pokemon.feature

import com.cobblemon.mod.common.api.net.Decodable
import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.api.properties.CustomPokemonProperty
import com.cobblemon.mod.common.api.properties.CustomPokemonPropertyType
import com.cobblemon.mod.common.client.gui.summary.featurerenderers.BarSummarySpeciesFeatureRenderer
import com.cobblemon.mod.common.client.gui.summary.featurerenderers.SummarySpeciesFeatureRenderer
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.asTranslated
import com.cobblemon.mod.common.util.cobblemonResource
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d

/**
 * A species feature value that's just an integer. Complex stuff.
 * @author Hiroku
 */
class IntSpeciesFeature(override var name: String) : SynchronizedSpeciesFeature, CustomPokemonProperty {
    var value = 0

    constructor() : this("")
    constructor(name: String, value: Int) : this(name) {
        this.value = value
    }

    override fun saveToNBT(pokemonNBT: NbtCompound): NbtCompound {
        pokemonNBT.putInt(name, value)
        return pokemonNBT
    }

    override fun loadFromNBT(pokemonNBT: NbtCompound): SynchronizedSpeciesFeature {
        value = pokemonNBT.getInt(name)
        return this
    }

    override fun saveToJSON(pokemonJSON: JsonObject): JsonObject {
        pokemonJSON.addProperty(name, value)
        return pokemonJSON
    }

    override fun loadFromJSON(pokemonJSON: JsonObject): SpeciesFeature {
        value = pokemonJSON.get(name).asInt
        return this
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeInt(value)
    }

    override fun decode(buffer: PacketByteBuf) {
        value = buffer.readInt()
    }

    override fun asString() = "$name=$value"
    override fun apply(pokemon: Pokemon) {
        val featureProvider = SpeciesFeatures.getFeature(name) ?: return
        if (featureProvider in SpeciesFeatures.getFeaturesFor(pokemon.species)) {
            val existingFeature = pokemon.getFeature<IntSpeciesFeature>(name)
            if (existingFeature != null) {
                existingFeature.value = value
            } else {
                pokemon.features.add(IntSpeciesFeature(name, value))
            }
            pokemon.updateAspects()
        }
    }

    override fun matches(pokemon: Pokemon) = pokemon.getFeature<IntSpeciesFeature>(name)?.value == value
}

class IntSpeciesFeatureProvider : SynchronizedSpeciesFeatureProvider<IntSpeciesFeature>,
    CustomPokemonPropertyType<IntSpeciesFeature> {
    class DisplayData : Encodable, Decodable {
        var name: String = ""

        @SerializedName(value = "colour" /* fuck you we use real english */, alternate = ["color"])
        var colour = Vec3d(255.0, 255.0, 255.0)
        var underlay: Identifier? = null
        var overlay: Identifier? = null

        override fun decode(buffer: PacketByteBuf) {
            name = buffer.readString()
            colour = Vec3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble())
            underlay = buffer.readNullable { buffer.readIdentifier() }
            overlay = buffer.readNullable { buffer.readIdentifier() }
        }

        override fun encode(buffer: PacketByteBuf) {
            buffer.writeString(name)
            buffer.writeDouble(colour.x)
            buffer.writeDouble(colour.y)
            buffer.writeDouble(colour.z)
            buffer.writeNullable(underlay) { _, value -> buffer.writeIdentifier(value) }
            buffer.writeNullable(overlay) { _, value -> buffer.writeIdentifier(value) }
        }
    }

    override var keys = listOf<String>()

    // Uses get() = true because that way there's no backing field. It MUST be true, this way no JSON trickery will overwrite it
    override val needsKey get() = true
    override var visible = false
    var default: Int? = null
    var min = 0
    var max = 100
    var display: DisplayData? = null
    var itemPoints: Map<Identifier, Int> = emptyMap()

    override fun fromString(value: String?) =
        value?.toIntOrNull()?.takeIf { it in min..max }?.let { IntSpeciesFeature(keys.first(), it) }

    override fun examples() = emptyList<String>()
    override fun invoke(buffer: PacketByteBuf, name: String): IntSpeciesFeature? {
        return if (name in keys) {
            IntSpeciesFeature(name, buffer.readInt())
        } else {
            null
        }
    }

    override fun invoke(pokemon: Pokemon): IntSpeciesFeature? {
        return get(pokemon) ?: default?.let { IntSpeciesFeature(keys.first(), it) }
    }

    override fun invoke(nbt: NbtCompound): IntSpeciesFeature? {
        return if (nbt.contains(keys.first())) {
            IntSpeciesFeature(keys.first(), nbt.getInt(keys.first()))
        } else {
            null
        }
    }

    override fun invoke(json: JsonObject): IntSpeciesFeature? {
        return if (json.has(keys.first())) {
            IntSpeciesFeature(keys.first(), json.get(keys.first()).asInt)
        } else {
            null
        }
    }

    override fun get(pokemon: Pokemon) = pokemon.features.filterIsInstance<IntSpeciesFeature>().find { it.name in keys }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeCollection(keys) { _, value -> buffer.writeString(value) }
        buffer.writeNullable(default) { _, value -> buffer.writeInt(value) }
        buffer.writeInt(min)
        buffer.writeInt(max)
        buffer.writeNullable(display) { _, value -> value.encode(buffer) }
        buffer.writeMap(itemPoints, { _, item -> buffer.writeString(item.toString()) }) { _, points ->
            buffer.writeInt(
                points
            )
        }
    }

    override fun decode(buffer: PacketByteBuf) {
        keys = buffer.readList { buffer.readString() }
        default = buffer.readNullable { buffer.readInt() }
        min = buffer.readInt()
        max = buffer.readInt()
        display = buffer.readNullable { DisplayData().also { it.decode(buffer) } }
        itemPoints = buffer.readMap({ Identifier(buffer.readString()) }) { buffer.readInt() }
    }

    override fun getRenderer(pokemon: Pokemon): SummarySpeciesFeatureRenderer<IntSpeciesFeature>? {
        return display?.let {
            BarSummarySpeciesFeatureRenderer(
                name = keys.first(),
                displayName = it.name.asTranslated(),
                min = min,
                max = max,
                colour = it.colour,
                underlay = it.underlay ?: cobblemonResource("textures/gui/summary/summary_stats_other_bar.png"),
                overlay = it.overlay ?: cobblemonResource("textures/gui/summary/summary_stats_generic_overlay.png"),
                pokemon = pokemon
            )
        }
    }
}