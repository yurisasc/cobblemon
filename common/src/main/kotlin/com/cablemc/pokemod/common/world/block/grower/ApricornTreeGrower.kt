/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.world.block.grower

import com.cablemc.pokemod.common.PokemodConfiguredFeatures
import com.cablemc.pokemod.common.api.apricorn.Apricorn
import net.minecraft.block.sapling.SaplingGenerator
import net.minecraft.util.math.random.Random
class ApricornTreeGrower(private val apricorn: Apricorn) : SaplingGenerator() {
    override fun getTreeFeature(random: Random, bl: Boolean) = when (this.apricorn) {
        Apricorn.BLACK -> PokemodConfiguredFeatures.BLACK_APRICORN_TREE
        Apricorn.BLUE -> PokemodConfiguredFeatures.BLUE_APRICORN_TREE
        Apricorn.GREEN -> PokemodConfiguredFeatures.GREEN_APRICORN_TREE
        Apricorn.PINK -> PokemodConfiguredFeatures.PINK_APRICORN_TREE
        Apricorn.RED -> PokemodConfiguredFeatures.RED_APRICORN_TREE
        Apricorn.WHITE -> PokemodConfiguredFeatures.WHITE_APRICORN_TREE
        Apricorn.YELLOW -> PokemodConfiguredFeatures.YELLOW_APRICORN_TREE
    }
}