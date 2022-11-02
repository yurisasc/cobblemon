/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.pokemon.evolution

import com.cablemc.pokemod.common.api.pokemon.evolution.PreEvolution
import com.cablemc.pokemod.common.pokemon.FormData
import com.cablemc.pokemod.common.pokemon.Species

data class PokemodPreEvolution(override val species: Species, override val form: FormData) : PreEvolution
