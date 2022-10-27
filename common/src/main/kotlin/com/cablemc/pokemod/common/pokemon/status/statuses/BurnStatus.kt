/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.pokemon.status.statuses

import com.cablemc.pokemod.common.pokemon.status.PersistentStatus
import com.cablemc.pokemod.common.util.pokemodResource
class BurnStatus : PersistentStatus(
    name = pokemodResource("burn"),
    showdownName = "brn",
    applyMessage = "pokemod.status.burn.apply",
    removeMessage = null,
    defaultDuration = IntRange(180, 300)
)