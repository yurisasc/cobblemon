/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.evolution.variants

import com.cobblemon.mod.common.api.conditional.RegistryLikeCondition
import com.cobblemon.mod.common.api.moves.MoveTemplate
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.pokemon.evolution.ContextEvolution
import com.cobblemon.mod.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.registry.BlockIdentifierCondition
import com.cobblemon.mod.common.util.codec.setCodec
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.block.Block
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import net.minecraft.world.World

/**
 * Represents a [ContextEvolution] with [RegistryLikeCondition] of type [Block] context.
 * These are triggered upon interaction with any [Block] that matches the given context.
 *
 * @property requiredContext The [RegistryLikeCondition] of type [Block] expected to match.
 * @author Licious
 * @since October 31st, 2022
 */
open class BlockClickEvolution(
    override val id: String,
    override val result: PokemonProperties,
    override val requiredContext: RegistryLikeCondition<Block>,
    override var optional: Boolean,
    override var consumeHeldItem: Boolean,
    override val requirements: MutableSet<EvolutionRequirement>,
    override val learnableMoves: MutableSet<MoveTemplate>
) : ContextEvolution<BlockClickEvolution.BlockInteractionContext, RegistryLikeCondition<Block>> {

    override fun testContext(pokemon: Pokemon, context: BlockInteractionContext): Boolean {
        return this.requiredContext.fits(context.block, context.world.registryManager.get(RegistryKeys.BLOCK))
    }

    override fun equals(other: Any?) = other is BlockClickEvolution && other.id.equals(this.id, true)

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + ADAPTER_VARIANT.hashCode()
        return result
    }

    data class BlockInteractionContext(
        val block: Block,
        val world: World
    )

    companion object {
        const val ADAPTER_VARIANT = "block_click"

        val CODEC: Codec<BlockClickEvolution> = RecordCodecBuilder.create { builder ->
            builder.group(
                Codec.STRING.fieldOf("id").forGetter(BlockClickEvolution::id),
                PokemonProperties.CODEC.fieldOf("result").forGetter(BlockClickEvolution::result),
                // ToDo: RegistryLikeCondition
                Codec.BOOL.optionalFieldOf("optional", true).forGetter(BlockClickEvolution::optional),
                Codec.BOOL.optionalFieldOf("consumeHeldItem", false).forGetter(BlockClickEvolution::optional),
                setCodec(EvolutionRequirement.CODEC).optionalFieldOf("requirements", hashSetOf()).forGetter(BlockClickEvolution::requirements),
                setCodec(MoveTemplate.CODEC).optionalFieldOf("learnableMoves", hashSetOf()).forGetter(BlockClickEvolution::learnableMoves)
            ).apply(builder, ::BlockClickEvolution)
        }

    }
}