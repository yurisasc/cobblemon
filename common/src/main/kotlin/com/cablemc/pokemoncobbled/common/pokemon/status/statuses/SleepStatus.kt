/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.pokemon.status.statuses

import com.cablemc.pokemoncobbled.common.pokemon.status.PersistentStatus
import com.cablemc.pokemoncobbled.common.util.cobbledResource

class SleepStatus : PersistentStatus(
    name = cobbledResource("sleep"),
    showdownName = "slp",
    applyMessage = "pokemoncobbled.status.sleep.apply",
    removeMessage = "pokemoncobbled.status.sleep.woke",
    defaultDuration = IntRange(180, 300)
)