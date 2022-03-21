package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.world.level.levelgen.feature.ApricornTreeFeature

interface CobbledFeatures {
    fun register()
    fun apricornTreeFeature() : ApricornTreeFeature
}