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
import net.minecraft.entity.LivingEntity
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import net.minecraft.world.World

/**
 * A [EntityQueryRequirement] for when a [Pokemon] is expected to be in a [World].
 *
 * @property condition The [RegistryLikeCondition] of the [World] the queried entity is expected to be in.
 * @author Licious
 * @since March 21st, 2022
 */
class WorldRequirement(val condition: RegistryLikeCondition<World>) : EntityQueryRequirement {

    override fun check(pokemon: Pokemon, queriedEntity: LivingEntity) = this.condition.fits(queriedEntity.world, queriedEntity.world.registryManager.get(RegistryKeys.WORLD))

    override val variant: Variant<EvolutionRequirement> = VARIANT

    companion object {

        val CODEC: Codec<WorldRequirement> = RegistryLikeCondition.createCodec { server()!!.registryManager.get(RegistryKeys.WORLD) }
            .xmap(::WorldRequirement, WorldRequirement::condition)

        val VARIANT: Variant<EvolutionRequirement> = Variant(cobblemonResource("world"), CODEC)

    }

}