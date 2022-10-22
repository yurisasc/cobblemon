/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.battle

import com.cablemc.pokemod.common.api.scheduling.after
import com.cablemc.pokemod.common.api.scheduling.lerp
import com.cablemc.pokemod.common.client.render.models.blockbench.PoseableEntityState
import com.cablemc.pokemod.common.entity.pokeball.EmptyPokeBallEntity
import com.cablemc.pokemod.common.pokeball.PokeBall

/**
 * Handles the state for a capture PokéBall in a battle on the client side.
 *
 * @author Hiroku
 * @since July 2nd, 2022
 */
class ClientBallDisplay(val pokeBall: PokeBall) : PoseableEntityState<EmptyPokeBallEntity>() {
    enum class Phase {
        SHRINKING,
        SHAKING,
        GROWING
    }

    var phase = Phase.SHRINKING
    var scale = 1F
    var started = false
    var finished = false

    fun start() {
        started = true
        after(seconds = 1F) {
            lerp(seconds = 0.3F) { scale = 1 - it }
            after(seconds = 0.3F) {
                phase = Phase.SHAKING
                lerp(seconds = 0.3F) { scale = it }
            }
        }
    }

    fun finish() {
        lerp(seconds = 0.3F) { scale = 1 - it }
        after(seconds = 0.3F) {
            phase = Phase.GROWING
            lerp(seconds = 0.3F) { scale = it }
            after(seconds = 0.3F) {
                finished = true
            }
        }
    }
}