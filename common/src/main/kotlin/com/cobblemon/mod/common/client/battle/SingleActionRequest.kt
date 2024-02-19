/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.battle

import com.cobblemon.mod.common.battles.*

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
                    val singleRequest = SingleActionRequest(targetable, request.side, moveSet, forceSwitch, !request.noCancel)
                    // Known quirk of Showdown. It'll ask for actions on fainted slots
                    // Need to find better place to fix this
                    // Probably should find a way to fix this serverside so clients don't
                    // need to deal with this silliness.
                    val pokemon = request.side?.pokemon?.firstOrNull {
                        it.uuid == targetable.battlePokemon?.uuid
                    }
                    if(pokemon != null && pokemon.condition.contains("fnt")) {
                        singleRequest.response = PassActionResponse
                    }
                    singleRequest
                }
            )

            return singleActionRequests
        }
    }

    var response: ShowdownActionResponse? = null
}