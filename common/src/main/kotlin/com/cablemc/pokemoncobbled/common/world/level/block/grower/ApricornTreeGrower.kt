package com.cablemc.pokemoncobbled.common.world.level.block.grower

import com.cablemc.pokemoncobbled.common.CobbledConfiguredFeatures
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import net.minecraft.core.Holder
import net.minecraft.world.level.block.grower.AbstractTreeGrower
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import java.util.*

class ApricornTreeGrower(private val color: String) : AbstractTreeGrower() {
    override fun getConfiguredFeature(random: Random, bl: Boolean): Holder<out ConfiguredFeature<*, *>> {
        return when (color) {
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
}