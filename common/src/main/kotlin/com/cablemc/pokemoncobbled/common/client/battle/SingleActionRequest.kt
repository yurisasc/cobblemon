package com.cablemc.pokemoncobbled.common.client.battle

import com.cablemc.pokemoncobbled.common.battles.ShowdownActionRequest
import com.cablemc.pokemoncobbled.common.battles.ShowdownActionResponse
import com.cablemc.pokemoncobbled.common.battles.ShowdownMoveset
import com.cablemc.pokemoncobbled.common.battles.ShowdownSide

class SingleActionRequest(
    val activePokemon: ActiveClientBattlePokemon,
    val side: ShowdownSide?,
    val moveSet: ShowdownMoveset?,
    val forceSwitch: Boolean,
    val canCancel: Boolean
) {
    companion object {
        fun composeFrom(actor: ClientBattleActor, request: ShowdownActionRequest): MutableList<SingleActionRequest> {
            val singleActionRequests = mutableListOf<SingleActionRequest>()
            singleActionRequests.addAll(
                request.iterate(actor.activePokemon) { targetable, moveSet, forceSwitch ->
                    SingleActionRequest(targetable, request.side, moveSet, forceSwitch, !request.noCancel)
                }
            )

            return singleActionRequests
        }
    }

    var response: ShowdownActionResponse? = null
}