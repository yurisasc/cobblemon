/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.pokemon.species

import com.cobblemon.mod.common.api.abilities.Ability
import com.cobblemon.mod.common.api.data.ShowdownIdentifiable
import com.cobblemon.mod.common.api.pokemon.stats.StatMap
import com.cobblemon.mod.common.api.registry.CobblemonRegistryElement
import com.cobblemon.mod.common.api.types.ElementalType

interface Species : CobblemonRegistryElement<Species>, ShowdownIdentifiable {

    fun nationalPokedexNumber(): Int

    fun types(): Pair<ElementalType, ElementalType?>

    fun abilities(): Set<Ability>

    fun baseStats(): StatMap

}