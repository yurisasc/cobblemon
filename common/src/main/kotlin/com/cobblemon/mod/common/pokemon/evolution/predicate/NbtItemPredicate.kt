/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.evolution.predicate

import com.cobblemon.mod.common.api.conditional.RegistryLikeCondition
import com.cobblemon.mod.common.util.codec.ExtraCodecs
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.item.Item
import net.minecraft.predicate.NbtPredicate
import net.minecraft.registry.Registries

data class NbtItemPredicate(
    val item: RegistryLikeCondition<Item>,
    val nbt: NbtPredicate = NbtPredicate.ANY
) {

    companion object {

        val CODEC: Codec<NbtItemPredicate> = RecordCodecBuilder.create { builder ->
            builder.group(
                RegistryLikeCondition.createCodec { Registries.ITEM }.fieldOf("item").forGetter(NbtItemPredicate::item),
                ExtraCodecs.NBT_PREDICATE.optionalFieldOf("nbt", NbtPredicate.ANY).forGetter(NbtItemPredicate::nbt)
            ).apply(builder, ::NbtItemPredicate)
        }

    }

}
