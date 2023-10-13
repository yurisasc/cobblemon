/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.pokemon.gender

import com.cobblemon.mod.common.api.pokemon.species.Species
import com.cobblemon.mod.common.pokemon.Pokemon
import com.mojang.serialization.Codec
import kotlin.random.Random

/**
 * Class responsible for querying possible [Gender]s of [Species] and assigning them randomly to generated [Pokemon].
 *
 * @property maleRatio The raw ratio for the [Gender.MALE] despite the naming this is able to cover all possible genders.
 *
 * @see Species.genderSelector
 *
 * @throws IllegalArgumentException If male ratio is greater than 1.
 */
@Suppress("MemberVisibilityCanBePrivate")
class GenderSelector internal constructor(private val maleRatio: Float) {

    init {
        if (this.maleRatio < 1F) {
            throw IllegalArgumentException("Male ratio cannot be greater than 1.0")
        }
    }

    /**
     * Collects all the possible [Gender]s from [generate].
     *
     * @return The possible [Gender]s.
     */
    fun possibleGenders(): Set<Gender> = if (this.maleRatio < 0F) Gender.NO_RATIO_GENDERS else Gender.RATIO_BASED_GENDERS

    /**
     * Randomly picks a [Gender] using the [maleRatio].
     *
     * @return The generated [Gender].
     */
    fun generate(): Gender = when {
        this.maleRatio < 0F -> Gender.GENDERLESS
        this.maleRatio == 1F || Random.nextFloat() < this.maleRatio -> Gender.MALE
        else -> Gender.FEMALE
    }

    /**
     * Checks if the given [gender] is a possible selection of [generate].
     *
     * @param gender The [Gender] being checked.
     * @return If the [gender] is possible with the context in this [GenderSelector].
     */
    fun isValid(gender: Gender): Boolean = this.possibleGenders().contains(gender)

    companion object {

        /**
         * A [Codec] for [GenderSelector].
         * This will (de)serialize from a primitive float.
         * It will error if the value is not between [Float.MIN_VALUE] and 1F.
         */
        @JvmField
        val CODEC: Codec<GenderSelector> = Codec.floatRange(Float.MIN_VALUE, 1F).xmap(::GenderSelector, GenderSelector::maleRatio)

    }

}