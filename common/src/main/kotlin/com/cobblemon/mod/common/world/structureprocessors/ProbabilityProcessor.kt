package com.cobblemon.mod.common.world.structureprocessors

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.block.BlockState
import net.minecraft.structure.StructurePlacementData
import net.minecraft.structure.StructureTemplate
import net.minecraft.structure.processor.StructureProcessor
import net.minecraft.structure.processor.StructureProcessorRule
import net.minecraft.structure.processor.StructureProcessorType
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.random.Random
import net.minecraft.world.WorldView

class ProbabilityProcessor(
    val probability: Float,
    val rules: List<StructureProcessorRule>,
) : StructureProcessor() {
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
        if (random.nextFloat() < probability) {
            val blockState = currentBlockInfo.state
            for (rule in rules) {
                if (rule.test(
                        blockState,
                        blockState,
                        pos,
                        pos,
                        pivot,
                        random
                    )) {
                    return StructureTemplate.StructureBlockInfo(currentBlockInfo.pos, rule.outputState, rule.getOutputNbt(random, currentBlockInfo.nbt))
                }
            }
        }
        return currentBlockInfo
    }

    override fun getType(): StructureProcessorType<*> {
        return CobblemonProcessorTypes.PROBABILITY_PROCESSOR
    }

    companion object {
        val CODEC: Codec<ProbabilityProcessor> = RecordCodecBuilder.create { instance ->
            instance
                .group(
                    PrimitiveCodec.FLOAT.fieldOf("probability").forGetter { it.probability },
                    StructureProcessorRule.CODEC.listOf().fieldOf("rules").forGetter { it.rules },
                )
                .apply(instance) { probability, rules ->
                    ProbabilityProcessor(
                        probability,
                        rules
                    )
                }
        }
    }
}
