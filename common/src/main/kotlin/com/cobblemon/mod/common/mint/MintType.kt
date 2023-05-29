/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.mint

import com.cobblemon.mod.common.CobblemonBlocks
import com.cobblemon.mod.common.CobblemonItems
import net.minecraft.block.Block
import net.minecraft.item.Item

enum class MintType {
    RED,
    BLUE,
    CYAN,
    PINK,
    GREEN,
    WHITE;

    fun getSeed(): Item {
        return when (this) {
            RED -> CobblemonItems.RED_MINT_SEEDS
            BLUE -> CobblemonItems.BLUE_MINT_SEEDS
            CYAN -> CobblemonItems.CYAN_MINT_SEEDS
            PINK -> CobblemonItems.PINK_MINT_SEEDS
            GREEN -> CobblemonItems.GREEN_MINT_SEEDS
            WHITE -> CobblemonItems.WHITE_MINT_SEEDS
        }
    }

    fun getLeaf(): Item {
        return when (this) {
            RED -> CobblemonItems.RED_MINT_LEAF
            BLUE -> CobblemonItems.BLUE_MINT_LEAF
            CYAN -> CobblemonItems.CYAN_MINT_LEAF
            PINK -> CobblemonItems.PINK_MINT_LEAF
            GREEN -> CobblemonItems.GREEN_MINT_LEAF
            WHITE -> CobblemonItems.WHITE_MINT_LEAF
        }
    }

    fun getCropBlock(): Block {
        return when (this) {
            RED -> CobblemonBlocks.RED_MINT
            BLUE -> CobblemonBlocks.BLUE_MINT
            CYAN -> CobblemonBlocks.CYAN_MINT
            PINK -> CobblemonBlocks.PINK_MINT
            GREEN -> CobblemonBlocks.GREEN_MINT
            WHITE -> CobblemonBlocks.WHITE_MINT
        }
    }
}