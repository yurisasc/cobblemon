package com.cablemc.pokemoncobbled.common.pokemon.evolution

import com.cablemc.pokemoncobbled.common.api.moves.MoveTemplate
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.ContextEvolution
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.world.item.ItemStack

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
    override val requiredContext: ItemStack,
    override val optional: Boolean,
    override val consumeHeldItem: Boolean,
    override val requirements: MutableSet<EvolutionRequirement>,
    override val learnableMoves: MutableSet<MoveTemplate>
) : ContextEvolution<ItemStack, ItemStack> {

    override fun testContext(pokemon: Pokemon, context: ItemStack): Boolean {
        return if (this.requiredContext.tag == null)
            ItemStack.isSame(context, this.requiredContext)
        else
            ItemStack.isSameItemSameTags(context, this.requiredContext)
    }

    companion object {

        internal const val ADAPTER_VARIANT = "item_interact"

    }

}