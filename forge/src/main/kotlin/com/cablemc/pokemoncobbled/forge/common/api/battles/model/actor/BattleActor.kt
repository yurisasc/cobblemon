package com.cablemc.pokemoncobbled.forge.common.api.battles.model.actor

import com.cablemc.pokemoncobbled.forge.common.api.storage.party.PartyStore
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