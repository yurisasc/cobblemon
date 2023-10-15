/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.evolution.requirements

import com.cobblemon.mod.common.api.conditional.RegistryLikeCondition
import com.cobblemon.mod.common.api.pokemon.evolution.adapters.Variant
import com.cobblemon.mod.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.evolution.requirements.template.EntityQueryRequirement
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.server
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.entity.LivingEntity
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import net.minecraft.world.biome.Biome

/**
 * A [EntityQueryRequirement] for when a [Pokemon] is expected to be in a certain [Biome].
 *
 * @property biomeCondition The [Identifier] of the [Biome] the queried entity is expected to be in.
 * @author Licious
 * @since March 21st, 2022
 */
class BiomeRequirement(val biomeCondition: RegistryLikeCondition<Biome>) : EntityQueryRequirement {

    override fun check(pokemon: Pokemon, queriedEntity: LivingEntity) = biomeCondition.fits(
        queriedEntity.world.getBiome(queriedEntity.blockPos).value(),
        queriedEntity.world.registryManager.get(RegistryKeys.BIOME)
    )

    override val variant: Variant<EvolutionRequirement> = VARIANT

    companion object {

        val CODEC: Codec<BiomeRequirement> = RecordCodecBuilder.create { builder ->
            builder.group(
                RegistryLikeCondition.createCodec { server()!!.registryManager.get(RegistryKeys.BIOME) }.fieldOf("biomeCondition").forGetter(BiomeRequirement::biomeCondition)
            ).apply(builder, ::BiomeRequirement)
        }

        internal val VARIANT: Variant<EvolutionRequirement> = Variant(cobblemonResource("biome"), CODEC)

    }
}