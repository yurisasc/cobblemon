/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.evolution.variants

import com.cobblemon.mod.common.api.moves.MoveTemplate
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.pokemon.evolution.ContextEvolution
import com.cobblemon.mod.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.evolution.predicate.NbtItemPredicate
import com.cobblemon.mod.common.registry.ItemIdentifierCondition
import net.minecraft.item.ItemStack
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import net.minecraft.world.World

/**
 * Represents a [ContextEvolution] with [NbtItemPredicate] context.
 * These are triggered upon interaction with any [ItemStack] that matches the given predicate.
 *
 * @property requiredContext The [NbtItemPredicate] expected to match.
 * @author Licious
 * @since March 20th, 2022
 */
open class ItemInteractionEvolution(
    override val id: String,
    override val result: PokemonProperties,
    override val requiredContext: NbtItemPredicate,
    override var optional: Boolean,
    override var consumeHeldItem: Boolean,
    override val requirements: MutableSet<EvolutionRequirement>,
    override val learnableMoves: MutableSet<MoveTemplate>,
) : ContextEvolution<ItemInteractionEvolution.ItemInteractionContext, NbtItemPredicate> {
    constructor(): this(
        id = "id",
        result = PokemonProperties(),
        requiredContext = NbtItemPredicate(ItemIdentifierCondition(Identifier("minecraft", "fish")), null),
        optional = true,
        consumeHeldItem = true,
        requirements = mutableSetOf(),
        learnableMoves = mutableSetOf()
    )

    override fun testContext(pokemon: Pokemon, context: ItemInteractionContext): Boolean =
        this.requiredContext.item.fits(context.stack.item, context.world.registryManager.get(RegistryKeys.ITEM))
        && this.requiredContext.nbt?.test(context.stack) ?: true

    override fun equals(other: Any?) = other is ItemInteractionEvolution && other.id.equals(this.id, true)

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + ADAPTER_VARIANT.hashCode()
        return result
    }

    data class ItemInteractionContext(
        val stack: ItemStack,
        val world: World
    )

    companion object {
        const val ADAPTER_VARIANT = "item_interact"
    }
}