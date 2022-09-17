/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.api.events.pokemon

import com.cablemc.pokemoncobbled.common.api.events.Cancelable
import com.cablemc.pokemoncobbled.common.api.moves.MoveTemplate
import com.cablemc.pokemoncobbled.common.api.pokemon.experience.ExperienceSource
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

/**
 * Event fired when experience is about to be gained. Cancelling this event prevents
 * any experience being added, and the amount of experience cna be hcanged from [experience].
 *
 * @author Hiroku
 * @since August 5th, 2022
 */
class ExperienceGainedPreEvent(
    val pokemon: Pokemon,
    val source: ExperienceSource,
    var experience: Int
) : Cancelable()

/**
 * Event fired when experience has been gained. Information about whether it leveled up or not is
 * available.
 *
 * @author Hiroku
 * @since August 5th, 2022
 */
class ExperienceGainedPostEvent(
    val pokemon: Pokemon,
    val source: ExperienceSource,
    val experience: Int,
    val previousLevel: Int,
    val currentLevel: Int,
    val learnedMoves: MutableSet<MoveTemplate>
)