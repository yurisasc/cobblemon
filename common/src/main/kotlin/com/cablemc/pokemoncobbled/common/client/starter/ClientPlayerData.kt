package com.cablemc.pokemoncobbled.common.client.starter

import java.util.UUID

data class ClientPlayerData(
    var starterLocked: Boolean = true,
    var starterSelected: Boolean = false,
    var starterUUID: UUID? = null
)