package com.cablemc.pokemoncobbled.common.pokemon.evolution.variants

import com.cablemc.pokemoncobbled.common.api.moves.MoveTemplate
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.ContextEvolution
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cablemc.pokemoncobbled.common.item.interactive.EvolutionItem
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

/**
 * Represents a [ContextEvolution] with [ItemStack] context.
 * These are triggered upon interaction with the target [ItemStack].
 *
 * @property requiredContext The [ItemStack] expected to be used.
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
) : ContextEvolution<EvolutionItem, Identifier> {

    override fun testContext(pokemon: Pokemon, context: EvolutionItem): Boolean {
        val contextKey = Registry.ITEM.getKey(context)
        println("Requires: $requiredContext, Got: $contextKey")
        return contextKey == this.requiredContext
    }

    override fun equals(other: Any?) = other is ItemInteractionEvolution && other.id.equals(this.id, true)

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + ADAPTER_VARIANT.hashCode()
        return result
    }

    companion object {

        internal const val ADAPTER_VARIANT = "item_interact"

    }

}