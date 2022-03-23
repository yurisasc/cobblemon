package com.cablemc.pokemoncobbled.common.pokemon.evolution

import com.cablemc.pokemoncobbled.common.api.moves.MoveTemplate
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.ContextEvolution
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.PassiveEvolution
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cablemc.pokemoncobbled.common.config.constraint.IntConstraint
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.google.gson.annotations.SerializedName
import net.minecraft.world.item.ItemStack

/**
 * Represents a [PassiveEvolution].
 * This can be triggered at any check as long as the [Pokemon] passes [LevelEvolution.isValid].
 *
 * @property levels The level range the [Pokemon] is expected to be in, if the range only has a single number the [Pokemon.level] will need to be equal or greater then it instead.
 * @author Licious
 * @since March 20th, 2022
 */
open class LevelEvolution(
    override val id: String,
    override val result: PokemonProperties,
    @IntConstraint(1, 100)
    @SerializedName("levels", alternate = ["level"])
    val levels: IntRange,
    override val optional: Boolean,
    override val consumeHeldItem: Boolean,
    override val requirements: MutableSet<EvolutionRequirement>,
    override val learnableMoves: MutableSet<MoveTemplate>
) : PassiveEvolution {

    constructor(
        id: String,
        result: PokemonProperties,
        level: Int,
        optional: Boolean,
        consumeHeldItem: Boolean,
        requirements: MutableSet<EvolutionRequirement>,
        learnableMoves: MutableSet<MoveTemplate>
    ) : this(id, result, level..level, optional, consumeHeldItem, requirements, learnableMoves)

    override fun attemptEvolution(pokemon: Pokemon) = this.isValid(pokemon) && super.attemptEvolution(pokemon)

    private fun isValid(pokemon: Pokemon) : Boolean {
        if (this.levels.first != this.levels.last)
            return pokemon.level in this.levels
        return pokemon.level >= this.levels.first
    }

    companion object {

        internal const val ADAPTER_VARIANT = "level_up"

    }

}