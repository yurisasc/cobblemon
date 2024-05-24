/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.fishing

import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.util.Identifier

data class FishingBait(
    val item: Identifier,
    val effects: Map<Identifier, Effect>,
) {
    data class Effect(
        val subcategory: Identifier?,
        val chance: Double = 0.0,
        val value: Double = 0.0
    )

    object Effects {
        val NATURE = cobblemonResource("nature")
        val IV = cobblemonResource("iv")
        val EV = cobblemonResource("ev")
        val BITE_TIME = cobblemonResource("bite_time")
        val GENDER_CHANCE = cobblemonResource("gender_chance")
        val LEVEL_RAISE = cobblemonResource("level_raise")
        val TERA = cobblemonResource("tera")
        val SHINY_REROLL = cobblemonResource("shiny_reroll")
        val HIDDEN_ABILITY_CHANCE = cobblemonResource("ha_chance")
        val POKEMON_CHANCE = cobblemonResource("pokemon_chance")
    }

}


