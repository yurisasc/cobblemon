/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.events.breeding

import com.cobblemon.mod.common.api.pokemon.breeding.BreedingResult
import com.cobblemon.mod.common.pokemon.Pokemon

data class BreedingResultEvent(
    val breedingResult: BreedingResult,
    val mother: Pokemon,
    val father: Pokemon
)