package com.cobblemon.mod.common.pokemon.status.statuses.volatile

import com.cobblemon.mod.common.pokemon.status.VolatileStatus
import com.cobblemon.mod.common.util.cobblemonResource

class AttractStatus : VolatileStatus(
    cobblemonResource("attract"),
    "attract",
    "cobblemon.battle.attract_start",
    "cobblemon.battle.attract_snapped"
)