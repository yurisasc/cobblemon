/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.world.gamerules

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.mixin.invoker.BooleanRuleInvoker
import net.minecraft.world.GameRules

object CobblemonGameRules {

    val DO_POKEMON_SPAWNING: GameRules.Key<GameRules.BooleanRule> = Cobblemon.implementation.registerGameRule("doPokemonSpawning", GameRules.Category.SPAWNING, BooleanRuleInvoker.`cobblemon$create`(true))

}