package com.cablemc.pokemoncobbled.common.pokemon.evolution

import com.cablemc.pokemoncobbled.common.api.moves.MoveTemplate
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.ContextEvolution
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cablemc.pokemoncobbled.common.item.interactive.EvolutionItem
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

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
    override val requiredContext: ResourceLocation,
    override var optional: Boolean,
    override var consumeHeldItem: Boolean,
    override val requirements: MutableSet<EvolutionRequirement>,
    override val learnableMoves: MutableSet<MoveTemplate>
) : ContextEvolution<EvolutionItem, ResourceLocation> {

    override fun testContext(pokemon: Pokemon, context: EvolutionItem): Boolean {
        val contextKey = Registry.ITEM.getKey(context)
        println("Requires: $requiredContext, Got: $contextKey")
        return contextKey == this.requiredContext
    }

    companion object {

        internal const val ADAPTER_VARIANT = "item_interact"

    }

}