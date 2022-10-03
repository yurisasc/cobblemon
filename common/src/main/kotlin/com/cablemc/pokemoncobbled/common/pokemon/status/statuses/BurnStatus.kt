package com.cablemc.pokemoncobbled.common.pokemon.status.statuses

import com.cablemc.pokemoncobbled.common.pokemon.status.PersistentStatus
import com.cablemc.pokemoncobbled.common.util.cobbledResource

class BurnStatus : PersistentStatus(
    name = cobbledResource("burn"),
    showdownName = "brn",
    applyMessage = "pokemoncobbled.status.burn.apply",
    removeMessage = null,
    defaultDuration = IntRange(180, 300)
)