package com.cobblemon.mod.common.pokemon.status.statuses.volatile

import com.cobblemon.mod.common.pokemon.status.VolatileStatus
import com.cobblemon.mod.common.util.cobblemonResource

class ConfuseStatus : VolatileStatus(
    cobblemonResource("confusion"),
    "confusion",
    "cobblemon.battle.confusion_start",
    "cobblemon.battle.confusion_snapped"
)