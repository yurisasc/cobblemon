/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.events.pokemon

import com.cablemc.pokemod.common.api.events.Cancelable
import com.cablemc.pokemod.common.pokemon.Pokemon
import net.minecraft.server.network.ServerPlayerEntity

/**
 * Event that is fired when a Player mounts a Pokemon to a shoulder
 *
 * @author Qu
 * @since 2022-01-26
 */
data class ShoulderMountEvent(
    val player: ServerPlayerEntity,
    val pokemon: Pokemon,
    val isLeft: Boolean
) : Cancelable()