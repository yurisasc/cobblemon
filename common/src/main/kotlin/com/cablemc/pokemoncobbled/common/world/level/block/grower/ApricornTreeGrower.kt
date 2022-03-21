package com.cablemc.pokemoncobbled.common.world.level.block.grower

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import net.minecraft.core.Holder
import net.minecraft.world.level.block.grower.AbstractTreeGrower
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import java.util.*

class ApricornTreeGrower(private val color: String) : AbstractTreeGrower() {
    override fun getConfiguredFeature(random: Random, bl: Boolean): Holder<out ConfiguredFeature<*, *>> {
        return when(color) {
            "black" -> Holder.direct(PokemonCobbled.cobbledConfiguredFeatures.blackApricornTree())
            "blue" -> Holder.direct(PokemonCobbled.cobbledConfiguredFeatures.blueApricornTree())
            "green" -> Holder.direct(PokemonCobbled.cobbledConfiguredFeatures.greenApricornTree())
            "pink" -> Holder.direct(PokemonCobbled.cobbledConfiguredFeatures.pinkApricornTree())
            "red" -> Holder.direct(PokemonCobbled.cobbledConfiguredFeatures.redApricornTree())
            "white" -> Holder.direct(PokemonCobbled.cobbledConfiguredFeatures.whiteApricornTree())
            "yellow" -> Holder.direct(PokemonCobbled.cobbledConfiguredFeatures.yellowApricornTree())
            else -> Holder.direct(PokemonCobbled.cobbledConfiguredFeatures.blackApricornTree())
        }
    }
}