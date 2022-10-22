/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.battle

import com.cablemc.pokemod.common.battles.ShowdownActionRequest
import com.cablemc.pokemod.common.battles.ShowdownActionResponse
import com.cablemc.pokemod.common.battles.ShowdownMoveset
import com.cablemc.pokemod.common.battles.ShowdownSide

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