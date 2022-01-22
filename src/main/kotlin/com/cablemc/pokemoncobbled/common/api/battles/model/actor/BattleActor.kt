package com.cablemc.pokemoncobbled.common.api.battles.model.actor

import com.cablemc.pokemoncobbled.common.api.storage.party.PartyStore
import java.util.*

open class BattleActor(
    val showdownId: String,
    val gameId: UUID,
    val party: PartyStore
) {

    open fun getName() : String {
        return "Bob"
    }

}