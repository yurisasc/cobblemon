package com.cablemc.pokemoncobbled.common.api.blocks

import net.minecraft.core.BlockPos
import net.minecraft.util.valueproviders.UniformInt
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState

open class EvolutionStoneOre(properties: Properties) : Block(properties) {
    companion object {
        const val NORMAL_DESTROY_TIME = 3.0F
        const val DEEPSLATE_DESTROY_TIME = 4.5F
        const val EXPLOSION_RESISTANCE = 3.0F
        val NORMAL_PROPERTIES: Properties = Properties.copy(Blocks.STONE)
            .requiresCorrectToolForDrops()
            .strength(NORMAL_DESTROY_TIME, EXPLOSION_RESISTANCE)
        val DEEPSLATE_PROPERTIES: Properties = Properties.copy(Blocks.STONE)
            .requiresCorrectToolForDrops()
            .strength(DEEPSLATE_DESTROY_TIME, EXPLOSION_RESISTANCE)
            .sound(SoundType.DEEPSLATE)
    }

    constructor(properties: Properties, xpRange: UniformInt) : this(properties) {
        this.xpRange = xpRange
    }

    var xpRange = UniformInt.of(0, 0)

    override fun getExpDrop(
        state: BlockState?,
        world: LevelReader?,
        pos: BlockPos?,
        fortune: Int,
        silktouch: Int
    ): Int {
        return if (silktouch == 0) xpRange.sample(RANDOM) else 0
    }
}