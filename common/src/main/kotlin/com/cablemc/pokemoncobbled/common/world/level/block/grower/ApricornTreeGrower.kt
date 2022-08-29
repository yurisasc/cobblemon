package com.cablemc.pokemoncobbled.common.world.level.block.grower

import com.cablemc.pokemoncobbled.common.CobbledConfiguredFeatures
import net.minecraft.block.sapling.SaplingGenerator
import net.minecraft.util.math.random.Random

class ApricornTreeGrower(private val color: String) : SaplingGenerator() {
    override fun getTreeFeature(random: Random, bl: Boolean) = when (color) {
            "black" -> CobbledConfiguredFeatures.BLACK_APRICORN_TREE
            "blue" -> CobbledConfiguredFeatures.BLUE_APRICORN_TREE
            "green" -> CobbledConfiguredFeatures.GREEN_APRICORN_TREE
            "pink" -> CobbledConfiguredFeatures.PINK_APRICORN_TREE
            "red" -> CobbledConfiguredFeatures.RED_APRICORN_TREE
            "white" -> CobbledConfiguredFeatures.WHITE_APRICORN_TREE
            "yellow" -> CobbledConfiguredFeatures.YELLOW_APRICORN_TREE
            else -> CobbledConfiguredFeatures.WHITE_APRICORN_TREE
        }
}