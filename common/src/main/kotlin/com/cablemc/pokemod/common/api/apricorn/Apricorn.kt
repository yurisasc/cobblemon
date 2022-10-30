/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.apricorn

import com.cablemc.pokemod.common.PokemodBlocks
import com.cablemc.pokemod.common.PokemodItems
import com.cablemc.pokemod.common.item.ApricornItem
import com.cablemc.pokemod.common.world.block.ApricornBlock
import com.cablemc.pokemod.common.world.block.ApricornSaplingBlock
import net.minecraft.item.BlockItem

/**
 * Contains the different Apricorn variants and util methods to get their item, block, seedling and sapling form.
 *
 * @author Licious
 * @since October 29th, 2022
 */
enum class Apricorn {

    BLACK,
    BLUE,
    GREEN,
    PINK,
    RED,
    WHITE,
    YELLOW;

    fun item(): ApricornItem = when(this) {
        BLACK -> PokemodItems.BLACK_APRICORN.get()
        BLUE -> PokemodItems.BLUE_APRICORN.get()
        GREEN -> PokemodItems.GREEN_APRICORN.get()
        PINK -> PokemodItems.PINK_APRICORN.get()
        RED -> PokemodItems.RED_APRICORN.get()
        WHITE -> PokemodItems.WHITE_APRICORN.get()
        YELLOW -> PokemodItems.YELLOW_APRICORN.get()
    }

    fun seed(): BlockItem = when(this) {
        BLACK -> PokemodItems.BLACK_APRICORN_SEED.get()
        BLUE -> PokemodItems.BLUE_APRICORN_SEED.get()
        GREEN -> PokemodItems.GREEN_APRICORN_SEED.get()
        PINK -> PokemodItems.PINK_APRICORN_SEED.get()
        RED -> PokemodItems.RED_APRICORN_SEED.get()
        WHITE -> PokemodItems.WHITE_APRICORN_SEED.get()
        YELLOW -> PokemodItems.YELLOW_APRICORN_SEED.get()
    }

    fun block(): ApricornBlock = when(this) {
        BLACK -> PokemodBlocks.BLACK_APRICORN.get()
        BLUE -> PokemodBlocks.BLUE_APRICORN.get()
        GREEN -> PokemodBlocks.GREEN_APRICORN.get()
        PINK -> PokemodBlocks.PINK_APRICORN.get()
        RED -> PokemodBlocks.RED_APRICORN.get()
        WHITE -> PokemodBlocks.WHITE_APRICORN.get()
        YELLOW -> PokemodBlocks.YELLOW_APRICORN.get()
    }

    fun sapling(): ApricornSaplingBlock = when(this) {
        BLACK -> PokemodBlocks.BLACK_APRICORN_SAPLING.get()
        BLUE -> PokemodBlocks.BLUE_APRICORN_SAPLING.get()
        GREEN -> PokemodBlocks.GREEN_APRICORN_SAPLING.get()
        PINK -> PokemodBlocks.PINK_APRICORN_SAPLING.get()
        RED -> PokemodBlocks.RED_APRICORN_SAPLING.get()
        WHITE -> PokemodBlocks.WHITE_APRICORN_SAPLING.get()
        YELLOW -> PokemodBlocks.YELLOW_APRICORN_SAPLING.get()
    }

}