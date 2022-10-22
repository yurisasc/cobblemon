/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.pokeball

import com.cablemc.pokemod.common.api.pokeball.catching.CatchRateModifier
import net.minecraft.util.Identifier

/**
 * Base poke ball object
 * It is intended that there is one poke ball object initialized for a given poke ball type.
 *
 * @property name the poke ball registry name
 * @property catchRateModifiers list of all [CatchRateModifier] that is applicable to the poke ball
 */
open class PokeBall(
    val name: Identifier,
    val catchRateModifiers: List<CatchRateModifier> = listOf()
)