/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.api.events.pokemon.evolution

import com.cablemc.pokemoncobbled.common.api.events.Cancelable
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.Evolution
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

/**
 * Fired when an evolution is accepted.
 * Canceling will not notify users nor remove the evolution from the pending list.
 *
 * @param pokemon The [Pokemon] about to evolve.
 * @param evolution The [Evolution] being used.
 *
 * @author Licious
 * @since April 28th, 2022
 */
data class EvolutionAcceptedEvent(
    val pokemon: Pokemon,
    val evolution: Evolution
) : Cancelable()