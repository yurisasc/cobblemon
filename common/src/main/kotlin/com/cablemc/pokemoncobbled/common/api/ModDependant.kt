/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.api

import com.cablemc.pokemoncobbled.common.PokemonCobbled

/**
 * Something that depends on some installed mod conditions.
 *
 * @author Hiroku
 * @since July 8th, 2022
 */
interface ModDependant {
    var neededInstalledMods: List<String>
    var neededUninstalledMods: List<String>

    fun isModDependencySatisfied(): Boolean {
        return if (neededInstalledMods.isNotEmpty() && neededInstalledMods.any { !PokemonCobbled.implementation.isModInstalled(it) }) {
            false
        } else if (neededUninstalledMods.isNotEmpty() && neededUninstalledMods.any { PokemonCobbled.implementation.isModInstalled(it) }) {
            false
        } else {
            true
        }
    }
}