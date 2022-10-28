/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.pokemon.evolution.variants

import com.cablemc.pokemod.common.api.moves.MoveTemplate
import com.cablemc.pokemod.common.api.pokemon.PokemonProperties
import com.cablemc.pokemod.common.api.pokemon.evolution.ContextEvolution
import com.cablemc.pokemod.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cablemc.pokemod.common.pokemon.Pokemon
import net.minecraft.util.Identifier

/**
 * Represents a [ContextEvolution] with [Identifier] context.
 * These are triggered upon interaction with any [EvolutionItem] whose [Identifier] identifier matches the given context.
 *
 * @property requiredContext The [Identifier] expected to match.
 * @author Licious
 * @since March 20th, 2022
 */
open class ItemInteractionEvolution(
    override val id: String,
    override val result: PokemonProperties,
    override val requiredContext: Identifier,
    override var optional: Boolean,
    override var consumeHeldItem: Boolean,
    override val requirements: MutableSet<EvolutionRequirement>,
    override val learnableMoves: MutableSet<MoveTemplate>
) : ContextEvolution<Identifier, Identifier> {
    constructor(): this(
        id = "id",
        result = PokemonProperties(),
        requiredContext = Identifier("minecraft", "fish"),
        optional = true,
        consumeHeldItem = true,
        requirements = mutableSetOf(),
        learnableMoves = mutableSetOf()
    )

    override fun testContext(pokemon: Pokemon, context: Identifier): Boolean {
        return context == this.requiredContext
    }

    override fun equals(other: Any?) = other is ItemInteractionEvolution && other.id.equals(this.id, true)

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + ADAPTER_VARIANT.hashCode()
        return result
    }

    companion object {
        const val ADAPTER_VARIANT = "item_interact"
    }
}