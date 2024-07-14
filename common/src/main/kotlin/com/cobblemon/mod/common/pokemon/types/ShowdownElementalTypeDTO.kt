/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.types

import com.cobblemon.mod.common.api.resistance.ResistanceMap
import com.cobblemon.mod.common.api.types.ElementalType

/**
 * Showdown type = TypeInfo
 *
 * @property id The showdown ID, represented by us as [ElementalType.showdownId]
 * @property damageTaken The damage taken from various effects, represented by us as [ResistanceMap].
 */
internal data class ShowdownElementalTypeDTO(
    val id: String,
    val damageTaken: Map<String, Int>
)
