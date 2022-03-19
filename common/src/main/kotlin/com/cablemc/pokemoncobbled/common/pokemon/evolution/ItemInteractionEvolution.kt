package com.cablemc.pokemoncobbled.common.pokemon.evolution

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.ContextEvolution
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement.ContextEvolutionRequirement
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement.EvolutionContext
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement.EvolutionRequirement
import net.minecraft.world.item.ItemStack

open class ItemInteractionEvolution(
    override val id: String,
    override val to: PokemonProperties,
    val item: ItemStack,
    override val requirements: List<EvolutionRequirement>,
) : ContextEvolution<ItemInteractionEvolution.Context> {

    override val optional = !PokemonCobbled.config.forceItemInteractionEvolution

    override val contextRequirements: List<ContextEvolutionRequirement<Context>> = listOf(ContextEvolutionRequirement { _, context -> ItemStack.isSameItemSameTags(this.item, context.item) })

    open class Context(val item: ItemStack) : EvolutionContext

}