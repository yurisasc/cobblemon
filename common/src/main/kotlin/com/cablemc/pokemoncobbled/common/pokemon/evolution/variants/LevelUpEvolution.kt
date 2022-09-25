/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.pokemon.evolution.variants

import com.cablemc.pokemoncobbled.common.api.moves.MoveTemplate
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.PassiveEvolution
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

/**
 * Represents a [PassiveEvolution].
 * This can be triggered at any check as long as the [Pokemon] passes [LevelUpEvolution.isValid].
 *
 * @property levels The level range the [Pokemon] is expected to be in, if the range only has a single number the [Pokemon.level] will need to be equal or greater then it instead.
 * @author Licious
 * @since March 20th, 2022
 */
open class LevelUpEvolution(
    override val id: String,
    override val result: PokemonProperties,
    override var optional: Boolean,
    override var consumeHeldItem: Boolean,
    override val requirements: MutableSet<EvolutionRequirement>,
    override val learnableMoves: MutableSet<MoveTemplate>
) : PassiveEvolution {

    /* Needed for old Gson versions that MC ships with */
    constructor(): this(
        id = "id",
        result = PokemonProperties(),
        optional = true,
        consumeHeldItem = true,
        requirements = mutableSetOf(),
        learnableMoves = mutableSetOf()
    )

    override fun equals(other: Any?) = other is LevelUpEvolution && other.id.equals(this.id, true)

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + ADAPTER_VARIANT.hashCode()
        return result
    }

    companion object {
        const val ADAPTER_VARIANT = "level_up"
    }
}