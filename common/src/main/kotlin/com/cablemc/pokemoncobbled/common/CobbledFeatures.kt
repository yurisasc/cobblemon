package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.world.feature.ApricornTreeFeature

interface CobbledFeatures {
    fun register()

    fun apricornTreeFeature() : ApricornTreeFeature
}