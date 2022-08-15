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
) : ContextEvolution<EvolutionItem, Identifier> {
    constructor(): this(
        id = "id",
        result = PokemonProperties(),
        requiredContext = Identifier("minecraft", "fish"),
        optional = true,
        consumeHeldItem = true,
        requirements = mutableSetOf(),
        learnableMoves = mutableSetOf()
    )

    override fun testContext(pokemon: Pokemon, context: EvolutionItem): Boolean {
        val contextKey = Registry.ITEM.getKey(context)
        return contextKey == this.requiredContext
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