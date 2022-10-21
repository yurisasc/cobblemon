/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.world.block.grower

import net.minecraft.block.sapling.SaplingGenerator
import net.minecraft.util.math.random.Random
class ApricornTreeGrower(private val color: String) : SaplingGenerator() {
    override fun getTreeFeature(random: Random, bl: Boolean) = when (color) {
            "black" -> com.cablemc.pokemod.common.PokemodConfiguredFeatures.BLACK_APRICORN_TREE
            "blue" -> com.cablemc.pokemod.common.PokemodConfiguredFeatures.BLUE_APRICORN_TREE
            "green" -> com.cablemc.pokemod.common.PokemodConfiguredFeatures.GREEN_APRICORN_TREE
            "pink" -> com.cablemc.pokemod.common.PokemodConfiguredFeatures.PINK_APRICORN_TREE
            "red" -> com.cablemc.pokemod.common.PokemodConfiguredFeatures.RED_APRICORN_TREE
            "white" -> com.cablemc.pokemod.common.PokemodConfiguredFeatures.WHITE_APRICORN_TREE
            "yellow" -> com.cablemc.pokemod.common.PokemodConfiguredFeatures.YELLOW_APRICORN_TREE
            else -> com.cablemc.pokemod.common.PokemodConfiguredFeatures.WHITE_APRICORN_TREE
        }
}