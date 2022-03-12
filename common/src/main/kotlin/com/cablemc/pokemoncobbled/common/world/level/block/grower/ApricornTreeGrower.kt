package com.cablemc.pokemoncobbled.common.world.level.block.grower

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import net.minecraft.world.level.block.grower.AbstractTreeGrower
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import java.util.*

class ApricornTreeGrower : AbstractTreeGrower() {
    override fun getConfiguredFeature(random: Random, bl: Boolean): ConfiguredFeature<*, *> {
        return PokemonCobbled.cobbledConfiguredFeatures.apricornTree()
    }
}