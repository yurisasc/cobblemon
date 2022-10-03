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

class FrozenStatus : PersistentStatus(
    name = cobbledResource("frozen"),
    showdownName = "frz",
    applyMessage = "pokemoncobbled.status.frozen.apply",
    removeMessage = "pokemoncobbled.status.frozen.thawed",
    defaultDuration = IntRange(180, 300)
)