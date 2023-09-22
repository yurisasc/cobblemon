/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.pokemon.stats

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.serialization.BufferSerializer
import com.cobblemon.mod.common.api.serialization.DataSerializer
import com.cobblemon.mod.common.pokemon.EVs
import com.cobblemon.mod.common.pokemon.IVs
import com.google.gson.JsonObject
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtOps
import net.minecraft.network.PacketByteBuf
import kotlin.jvm.optionals.getOrDefault

/**
 * Represents a map-like structure that is used solely for the purpose of various stat-like Pokémon data structures.
 *
 * @property variant The [Variant] of this map.
 * @property stats The base map used for this map.
 *
 * @see <a href="https://bulbapedia.bulbagarden.net/wiki/Base_stats">Base stats Bulbapedia entry</a>
 * @see <a href="https://bulbapedia.bulbagarden.net/wiki/Effort_values">Effort values Bulbapedia entry</a>
 * @see <a href="https://bulbapedia.bulbagarden.net/wiki/Individual_values">Individual values Bulbapedia entry</a>
 *
 */
@Suppress("unused")
open class StatMap internal constructor(
    val variant: Variant,
    private val stats: HashMap<Stat, Int>
) : Map<Stat, Int>, DataSerializer<NbtCompound, JsonObject>, BufferSerializer {

    init {
        if (!Cobblemon.statProvider.ofType(Stat.Type.PERMANENT).all(this.stats::containsKey)) {
            throw IllegalArgumentException("Cannot instance ${this::class.simpleName} without all ${Stat.Type.PERMANENT.name} ${Stat::class.simpleName}")
        }
        if (!this.stats.values.any { value -> this.variant.fits(value) }) {
            throw IllegalArgumentException("Cannot instance ${this::class.simpleName} with values not in range [${this.variant.minValue}-${this.variant.maxValue}]")
        }
    }

    override val entries: Set<Map.Entry<Stat, Int>>
        get() = this.stats.entries
    override val keys: Set<Stat>
        get() = this.stats.keys
    override val size: Int
        get() = this.stats.size
    override val values: Collection<Int>
        get() = this.stats.values

    /**
     * A constraint of implementing [Map].
     * This is impossible to be false.
     *
     * @return Always true.
     */
    override fun isEmpty(): Boolean = false

    /**
     * Returns the value corresponding to the given [key].
     * Due to implementation details unlike a typical [Map.get] this will never return null.
     *
     * @param key The [Stat] being checked.
     * @return The value attached to the [key].
     */
    override fun get(key: Stat): Int = this.stats[key]!!

    override fun containsValue(value: Int): Boolean = this.stats.containsValue(value)

    override fun containsKey(key: Stat): Boolean = this.stats.containsKey(key)

    override fun loadFromNBT(nbt: NbtCompound) {
        this.codec().decode(NbtOps.INSTANCE, nbt)
            .result()
            .ifPresent { pair -> this.putAll(pair.first) }
    }

    override fun saveToNBT(): NbtCompound {
        val result = this.codec().encodeStart(NbtOps.INSTANCE, this)
            .result()
            .getOrDefault(NbtCompound())
        // It's safe to cast it will always result in a NbtCompound
        return result as NbtCompound
    }

    override fun loadFromJson(json: JsonObject) {
        this.codec().decode(JsonOps.INSTANCE, json)
            .result()
            .ifPresent { pair -> this.putAll(pair.first) }
    }

    override fun saveToJson(): JsonObject {
        val result = this.codec().encodeStart(JsonOps.INSTANCE, this)
            .result()
            .getOrDefault(JsonObject())
        // It's safe to cast it will always result in a JsonObject
        return result as JsonObject
    }

    override fun saveToBuffer(buffer: PacketByteBuf, toClient: Boolean) {
        buffer.writeMap(this.stats, { pb, stat -> pb.writeIdentifier(stat.identifier) }, PacketByteBuf::writeInt)
    }

    override fun loadFromBuffer(buffer: PacketByteBuf) {
        // This is safe to do as the server will have sanitized the values.
        this.stats.clear()
        this.stats.putAll(buffer.readMap({ pb -> Cobblemon.statProvider.decode(pb) }, PacketByteBuf::readInt))
    }

    /**
     * Resets the value for the given [key] to the [Variant.minValue] of [variant].
     *
     * @param key The [Stat] being reset.
     * @return The previous attached value.
     *
     * @throws IllegalArgumentException If the [key] fails [validateKey].
     */
    fun reset(key: Stat): Int {
        this.validateKey(key)
        val existing = this.stats[key]!!
        this.stats[key] = this.variant.minValue
        return existing
    }

    /**
     * Resets all values of this map to the [Variant.minValue] of [variant].
     */
    fun reset() {
        Cobblemon.statProvider.ofType(Stat.Type.PERMANENT).forEach(this::reset)
    }

    /**
     * Merges key-value pairs from a map into this [StatMap].
     *
     * @param from The map being merged.
     *
     * @throws IllegalArgumentException If any value of [from] fails [validateValue].
     */
    fun putAll(from: Map<out Stat, Int>) {
        from.forEach(this::put)
    }

    /**
     * Puts the given [value] on the given [key] [Stat] of this [StatMap].
     *
     * @param key The [Stat] being set.
     * @param value The value being set to the [key].
     * @return The previously attached value.
     *
     * @throws IllegalArgumentException If the [value] fails [validateValue].
     */
    fun put(key: Stat, value: Int): Int {
        this.validateValue(value)
        val existing = this.stats[key]!!
        this.stats[key] = value
        return existing
    }

    /**
     * Resolves the [Codec] for this instance.
     * This is done based on the [variant].
     *
     * @return The [Codec] for this specific [StatMap] setup.
     *
     * @see StatMap.BASE_STATS_CODEC
     * @see StatMap.EV_STATS_CODEC
     * @see StatMap.IV_STATS_CODEC
     */
    fun codec(): Codec<StatMap> = when (this.variant) {
        Variant.BASE_STATS -> BASE_STATS_CODEC
        Variant.IV -> IV_STATS_CODEC
        Variant.EV -> EV_STATS_CODEC
    }

    /**
     * Checks if a [Stat.Type] is [Stat.Type.PERMANENT].
     *
     * @param stat The [Stat] being checked.
     *
     * @throws IllegalArgumentException If the [Stat.Type] is not [Stat.Type.PERMANENT].
     */
    protected open fun validateKey(stat: Stat) {
        if (stat.type != Stat.Type.PERMANENT) {
            throw IllegalArgumentException("Unsupported stat type of ${stat.type.name} for ${stat.identifier}")
        }
    }

    /**
     * Checks if a value is possible for this map.
     *
     * @param value The value being assigned.
     *
     * @throws IllegalArgumentException If the [value] doesn't fulfill [Variant.fits] of the [variant].
     */
    protected open fun validateValue(value: Int) {
        if (!this.variant.fits(value)) {
            throw IllegalArgumentException("Cannot accept $value, a value must be in range [${this.variant.minValue}-${this.variant.maxValue}]")
        }
    }

    /**
     * Represents a type of data collected inside [StatMap].
     *
     * @property minValue The minimum possible value for a value of a [StatMap].
     * @property maxValue The maximum possible value for a value of a [StatMap].
     */
    enum class Variant(val minValue: Int, val maxValue: Int) {

        /**
         * Used to represent [base stats](https://bulbapedia.bulbagarden.net/wiki/Base_stats).
         */
        BASE_STATS(1, Int.MAX_VALUE),

        /**
         * Used to represent [effort values](https://bulbapedia.bulbagarden.net/wiki/Effort_values).
         */
        EV(0, EVs.MAX_STAT_VALUE),

        /**
         * Used to represent [individual values](https://bulbapedia.bulbagarden.net/wiki/Individual_values).
         */
        IV(0, IVs.MAX_VALUE);

        /**
         * Checks if the given [value] fits within the range created by [minValue] & [maxValue].
         *
         * @param value The value being checked.
         * @return If the value can fit in this variant.
         */
        fun fits(value: Int): Boolean = value >= this.minValue && value <= this.maxValue

    }

    companion object {

        /**
         * A [Codec] for a [StatMap] of the [Variant.BASE_STATS] variant.
         */
        @JvmField
        val BASE_STATS_CODEC: Codec<StatMap> = this.createCodec(Variant.BASE_STATS)

        /**
         * A [Codec] for a [StatMap] of the [Variant.EV] variant.
         */
        @JvmField
        val EV_STATS_CODEC: Codec<StatMap> = this.createCodec(Variant.EV)

        /**
         * A [Codec] for a [StatMap] of the [Variant.IV] variant.
         */
        @JvmField
        val IV_STATS_CODEC: Codec<StatMap> = this.createCodec(Variant.IV)

        private fun createCodec(variant: Variant): Codec<StatMap> = Codec.unboundedMap(Stat.CODEC, Codec.intRange(variant.minValue, variant.maxValue))
            .xmap({ map -> StatMap(variant, HashMap(map)) }, { statMap -> statMap })

    }

}