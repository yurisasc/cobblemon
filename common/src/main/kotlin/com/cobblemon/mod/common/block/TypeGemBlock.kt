package com.cobblemon.mod.common.block

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.block.*
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView

class TypeGemBlock(
    settings: Settings,
    stage: Int,
    height: Int,
    xzOffset: Int,
    nextStage: Block?,
    val baseBlock: Block
) : GrowableStoneBlock(settings, stage, height, xzOffset, nextStage) {
    companion object {
        val CODEC = RecordCodecBuilder.mapCodec<TypeGemBlock> {
            it.group(
                createSettingsCodec(),
                PrimitiveCodec.INT.fieldOf("stage").forGetter { it.stage },
                PrimitiveCodec.INT.fieldOf("height").forGetter { it.height },
                PrimitiveCodec.INT.fieldOf("xzOffset").forGetter { it.xzOffset },
                Block.CODEC.fieldOf("nextStage").forGetter { it.nextStage },
                Block.CODEC.fieldOf("base_block").forGetter { it.baseBlock }
            ).apply(it, ::TypeGemBlock)
        }
    }

    override fun canGrow(pos: BlockPos, world: BlockView): Boolean {
        val state = world.getBlockState(pos)
        val dir = state.get(FACING).opposite
        return (world.getBlockState(pos.offset(dir)).block == baseBlock)
    }

    override fun getCodec(): MapCodec<TypeGemBlock> = CODEC
}