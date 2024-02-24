/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.pokedex

import com.cobblemon.mod.common.api.pokedex.trackeddata.FormTrackedData
import net.minecraft.util.Identifier

/**
 * TrackedData of a specific form of a specific species
 *
 * @author Apion
 * @since February 24, 2024
 */
class FormPokedexEntry {
    var knowledge: PokedexProgress = PokedexProgress.NONE
    var formStats = mutableMapOf<Identifier, FormTrackedData>()
}