/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.evolution.requirements

import com.cobblemon.mod.common.api.pokemon.evolution.adapters.Variant
import com.cobblemon.mod.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.evolution.predicate.NbtItemPredicate
import com.cobblemon.mod.common.util.cobblemonResource
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.registry.Registries

/**
 * An [EvolutionRequirement] for a [Pokemon.heldItem].
 *
 * @property itemCondition The [NbtItemPredicate] expected to match the [Pokemon.heldItem].
 * @author Licious
 * @since March 21st, 2022
 */
class HeldItemRequirement(val itemCondition: NbtItemPredicate) : EvolutionRequirement {

    override fun check(pokemon: Pokemon): Boolean = this.itemCondition.item.fits(pokemon.heldItemNoCopy().item, Registries.ITEM) && this.itemCondition.nbt.test(pokemon.heldItemNoCopy())

    override val variant: Variant<EvolutionRequirement> = VARIANT

    companion object {

        val CODEC: Codec<HeldItemRequirement> = RecordCodecBuilder.create { builder ->
            builder.group(
                NbtItemPredicate.CODEC.fieldOf("itemCondition").forGetter(HeldItemRequirement::itemCondition)
            ).apply(builder, ::HeldItemRequirement)
        }

        internal val VARIANT: Variant<EvolutionRequirement> = Variant(cobblemonResource("held_item"), CODEC)

    }
}