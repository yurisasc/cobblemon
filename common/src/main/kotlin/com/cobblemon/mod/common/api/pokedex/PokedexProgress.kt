
/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.pokedex

import net.minecraft.util.StringIdentifiable

/**
 * Contains stats about a specific pokemon for putting in the pokedex
 *
 * @author JPAK, Apion
 * @since February 21, 2024
 */
enum class PokedexProgress : StringIdentifiable {
    NONE,
    ENCOUNTERED,
    CAUGHT;
    override fun asString(): String {
        return this.name
    }
}
