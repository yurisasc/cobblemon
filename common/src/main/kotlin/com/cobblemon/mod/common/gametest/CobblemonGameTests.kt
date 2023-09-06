/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.gametest

import com.cobblemon.mod.common.CobblemonEntities
import com.cobblemon.mod.common.api.pokemon.PokemonProperties.Companion.parse
import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.IVs
import net.minecraft.test.GameTest
import net.minecraft.test.TestContext
import net.minecraft.util.math.BlockPos

class CobblemonGameTests {

    @GameTest(templateName = "")
    fun spawnBidoof(context: TestContext) {
        context.addInstantFinalTask {
            val bidoof = parse("bidoof").create()
            bidoof.ivs[Stats.ATTACK] = 31

            val target = BlockPos(context.getRelativePos(BlockPos(0, 0, 0)))
            context.expectEntity(CobblemonEntities.POKEMON)
            context.expectEntityWithData(target, CobblemonEntities.POKEMON, PokemonEntity::pokemon, bidoof)
        }
    }

}
