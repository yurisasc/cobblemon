package com.cablemc.pokemoncobbled.common.client.starter

import java.util.UUID

data class ClientPlayerData(
    var promptStarter: Boolean = true,
    var starterLocked: Boolean = true,
    var starterSelected: Boolean = false,
    var starterUUID: UUID? = null
)