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