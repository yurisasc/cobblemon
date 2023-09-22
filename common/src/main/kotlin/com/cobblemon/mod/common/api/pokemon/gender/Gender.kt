/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.pokemon.gender

import com.cobblemon.mod.common.api.data.ShowdownIdentifiable
import com.cobblemon.mod.common.api.pokemon.species.Species
import com.cobblemon.mod.common.pokemon.Pokemon
import java.util.*

/**
 * Represents the [Gender] of a [Pokemon].
 * This is taken into account by [Species] when generating the [Pokemon].
 *
 * @property showdownId The ID of the [Gender] in Showdown.
 *
 * @see Species.genderSelector
 * @see Pokemon.gender
 * @see GenderSelector
 */
enum class Gender(private val showdownId: String) : ShowdownIdentifiable {

    MALE("M"),
    FEMALE("F"),
    GENDERLESS("N");
    override fun showdownId(): String = this.showdownId

    companion object {

        /**
         * The [Gender]s when a ratio is attached to a [GenderSelector].
         *
         * @see GenderSelector.maleRatio
         */
        @JvmField
        val RATIO_BASED_GENDERS: Set<Gender> = EnumSet.of(MALE, FEMALE)

        /**
         * The [Gender]s when no ratio is attached to a [GenderSelector].
         *
         * @see GenderSelector.maleRatio
         */
        @JvmField
        val NO_RATIO_GENDERS: Set<Gender> = EnumSet.of(GENDERLESS)

    }

}