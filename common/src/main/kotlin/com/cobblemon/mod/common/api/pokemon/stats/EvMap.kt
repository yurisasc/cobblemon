/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.pokemon.stats

import com.mojang.serialization.Codec
import kotlin.math.absoluteValue
import kotlin.math.min

/**
 * An implementation of [StatMap] aimed solely at the EV data structure.
 * This exists due to a few extra constraints present in EVs regarding the total value of the structure.
 *
 * @see <a href="https://bulbapedia.bulbagarden.net/wiki/Effort_values">Effort values Bulbapedia entry</a>
 */
@Suppress("MemberVisibilityCanBePrivate")
class EvMap internal constructor(stats: HashMap<Stat, Int>) : StatMap(Variant.EV, stats) {

    init {
        if (this.values.sum() > MAX_TOTAL) {
            throw IllegalArgumentException("Cannot instance ${this::class.simpleName} with values total exceeding $MAX_TOTAL")
        }
    }

    /**
     * Inherits the logic of [StatMap.validateValue].
     * This has the added constraint of the maximum possible total value being [MAX_TOTAL].
     *
     * @see StatMap.validateValue
     */
    override fun validateValue(key: Stat, value: Int) {
        super.validateValue(key, value)
        val existing = this[key]
        val current = this.values.sum() - existing
        if (current + value > MAX_TOTAL) {
            throw IllegalArgumentException("The total of the values of an ${this::class.simpleName} cannot exceed $MAX_TOTAL")
        }
    }

    /**
     * Adds to the given [stat].
     *
     * @param stat The [Stat] being added to.
     * @param amount The amount being added. This value will be sanitized with [Int.absoluteValue].
     * @param coerceSafe Should the value be coerced to be safe to add?
     * @return The new resulting value of [stat].
     *
     * @throws IllegalArgumentException if [validateValue] fails.
     *
     * @see subtract
     * @see mutate
     */
    fun add(stat: Stat, amount: Int, coerceSafe: Boolean): Int = this.mutate(stat, amount.absoluteValue, coerceSafe)

    /**
     * Subtracts from the given [stat].
     *
     * @param stat The [Stat] being subtracted from.
     * @param amount The amount being subtracted. This value will be sanitized with [Int.absoluteValue].
     * @param coerceSafe Should the value be coerced to be safe to subtract?
     * @return The new resulting value of [stat].
     *
     * @throws IllegalArgumentException if [validateValue] fails.
     *
     * @see add
     * @see mutate
     */
    fun subtract(stat: Stat, amount: Int, coerceSafe: Boolean): Int = this.mutate(stat, amount.absoluteValue, coerceSafe)

    /**
     * Mutates the given [stat].
     *
     * @param stat The [Stat] being mutated.
     * @param amount The amount being used in the mutation operation.
     * @param coerceSafe Should the value be coerced to be safe to operate with?
     * @return The new resulting value of [stat].
     *
     * @throws IllegalArgumentException if [validateValue] fails.
     *
     * @see add
     * @see subtract
     */
    fun mutate(stat: Stat, amount: Int, coerceSafe: Boolean): Int {
        var result = this[stat] + amount
        if (coerceSafe) {
            result = result.coerceIn(this.variant.minValue, min(this.variant.maxValue, this.remaining()))
        }
        this.put(stat, result)
        return result
    }

    /**
     * Resolves how many EVs can still be allocated to this structure.
     *
     * @return How many EVs can still be allocated.
     */
    fun remaining(): Int = MAX_TOTAL - this.values.sum()

    companion object {

        /**
         * A [Codec] for a [EvMap].
         */
        @JvmField
        val EV_STATS_CODEC: Codec<EvMap> = Codec.unboundedMap(Stat.CODEC, Codec.intRange(Variant.EV.minValue, Variant.EV.maxValue))
            .xmap({ map -> EvMap(HashMap(map)) }, { evMap -> evMap })

        /**
         * The maximum total EV values.
         */
        const val MAX_TOTAL = 510

        /**
         * Creates a [EvMap] with the given params.
         *
         * @param map The base map to take values from.
         * @return The generated [EvMap].
         *
         * @throws IllegalArgumentException If the constraints seen in [StatMap.create] are not fulfilled or if the total of the values exceeds [MAX_TOTAL].
         */
        fun create(map: Map<Stat, Int>): EvMap = EvMap(HashMap(map))

    }

}