/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.events.spawning

import com.cobblemon.mod.common.api.events.Cancelable
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnAction

/**
 * Event fired when a Pokémon is about to spawn. Canceling this event will prevent it from spawning.
 *
 * @author Hiroku
 * @since February 7th, 2023
 */
class SpawnPokemonEvent(
    val spawnAction: PokemonSpawnAction
) : Cancelable()