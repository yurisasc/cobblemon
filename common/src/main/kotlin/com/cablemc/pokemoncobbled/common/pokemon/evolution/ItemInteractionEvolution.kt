package com.cablemc.pokemoncobbled.common.pokemon.evolution

import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.ContextEvolution
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.world.item.ItemStack

open class ItemInteractionEvolution(
    override val id: String,
    override val result: PokemonProperties,
    override val requiredContext: ItemStack,
    override val optional: Boolean,
    override val consumeHeldItem: Boolean,
    override val requirements: List<EvolutionRequirement>
) : ContextEvolution<ItemStack> {

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