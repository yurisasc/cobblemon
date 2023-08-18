/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.world.structureprocessors

import com.cobblemon.mod.common.util.random
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.block.BlockState
import net.minecraft.structure.StructurePlacementData
import net.minecraft.structure.StructureTemplate
import net.minecraft.structure.processor.StructureProcessor
import net.minecraft.structure.processor.StructureProcessorType
import net.minecraft.structure.rule.RuleTest
import net.minecraft.structure.rule.blockentity.PassthroughRuleBlockEntityModifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.random.Random
import net.minecraft.world.WorldView

/**
 * A [StructureProcessor] that picks a number of [BlockState]s per structure to replace other blocks with
 *
 * Basically, each structure is assigned numStates BlockStates from target states
 * If a block matches any of the rules, it is overwritten by one of the BlockStates assigned to the
 * blocks structure
 *
 * @param targetStates The states for a structure to select from
 * @param numStates The number of states for a structure to select
 * @param rules The list of rules to check for each block
 * @param probability The probability for this processor to run
 */
class RandomizedStructureMappedBlockStateProcessor(
    val targetStates: List<BlockState>,
    val numStates: Int,
    val rules: List<RuleTest>,
    val probability: Float = 1.0f
) : StructureProcessor() {
    val CACHE_LOADER = object : CacheLoader<StructurePlacementData, List<BlockState>>() {
        override fun load(key: StructurePlacementData): List<BlockState> {
            return targetStates.random(3)
        }
    }
    val cache = CacheBuilder.newBuilder().maximumSize(4).build(CACHE_LOADER)

    override fun process(
        world: WorldView?,
        pos: BlockPos?,
        pivot: BlockPos?,
        originalBlockInfo: StructureTemplate.StructureBlockInfo?,
        currentBlockInfo: StructureTemplate.StructureBlockInfo?,
        data: StructurePlacementData?
    ): StructureTemplate.StructureBlockInfo? {
        val random = Random.create(
            MathHelper.hashCode(
                currentBlockInfo!!.pos()
            )
        );
        if (random.nextFloat() <= probability) {
            for (rule in rules) {
                if (rule.test(currentBlockInfo.state, random)) {
                    val index = random.nextInt(3)
                    val targetState = cache.get(data!!)[index]
                    return StructureTemplate.StructureBlockInfo(
                        currentBlockInfo.pos,
                        targetState,
                        PassthroughRuleBlockEntityModifier.INSTANCE.modifyBlockEntityNbt(
                            random,
                            currentBlockInfo.nbt
                        )
                    )
                }
            }
        }
        else {
            data?.removeProcessor(this)
        }
        return currentBlockInfo
    }

    override fun getType(): StructureProcessorType<*> {
        return CobblemonProcessorTypes.RANDOM_POOLED_STATES
    }

    companion object {
        val CODEC: Codec<RandomizedStructureMappedBlockStateProcessor> = RecordCodecBuilder.create { instance ->
            instance
                .group(
                    BlockState.CODEC.listOf().fieldOf("targetStates").forGetter { it.targetStates },
                    PrimitiveCodec.INT.fieldOf("numStates").forGetter { it.numStates },
                    RuleTest.TYPE_CODEC.listOf().fieldOf("rules").forGetter { it.rules },
                    PrimitiveCodec.FLOAT.fieldOf("probability").forGetter { it.probability }
                )
                .apply(instance) { targetStates, numStates, rules, probability ->
                    RandomizedStructureMappedBlockStateProcessor(
                        targetStates,
                        numStates,
                        rules,
                        probability
                    )
                }
        }
    }
}
