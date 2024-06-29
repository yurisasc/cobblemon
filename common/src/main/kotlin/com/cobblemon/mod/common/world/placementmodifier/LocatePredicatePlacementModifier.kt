/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.world.placementmodifier

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import java.util.stream.Stream
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.util.math.random.Random
import net.minecraft.world.gen.blockpredicate.BlockPredicate
import net.minecraft.world.gen.feature.FeaturePlacementContext
import net.minecraft.world.gen.placementmodifier.PlacementModifier
import net.minecraft.world.gen.placementmodifier.PlacementModifierType

class LocatePredicatePlacementModifier(
    val predicate: BlockPredicate,
    val maxTries: Int,
    val xzRange: Int,
    val yRange: Int
) : PlacementModifier() {
    companion object {
        val MODIFIER_CODEC: MapCodec<LocatePredicatePlacementModifier> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                BlockPredicate.BASE_CODEC.fieldOf("predicate").forGetter {it.predicate},
                PrimitiveCodec.INT.fieldOf("maxTries").forGetter {it.maxTries},
                PrimitiveCodec.INT.fieldOf("xzRange").forGetter {it.xzRange},
                PrimitiveCodec.INT.fieldOf("yRange").forGetter {it.yRange}
            )
            .apply(instance) {predicate, maxTries, xzRange, yRange ->
                LocatePredicatePlacementModifier(predicate, maxTries, xzRange, yRange)
            }
        }
    }
    override fun getPositions(
        context: FeaturePlacementContext,
        random: RandomSource,
        pos: BlockPos
    ): Stream<BlockPos> {
        for (i in 0..maxTries) {
            val newPos = pos.add(random.nextIntBetweenInclusive(0, xzRange), random.nextIntBetweenInclusive(-yRange, yRange), random.nextIntBetweenInclusive(0, xzRange))
            if (predicate.test(context.world, newPos)) {
                return Stream.of(newPos)
            }
        }
        return Stream.empty()
    }

    override fun getType(): PlacementModifierType<*> {
        return CobblemonPlacementModifierTypes.LOCATE_PREDICATE
    }
}
