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