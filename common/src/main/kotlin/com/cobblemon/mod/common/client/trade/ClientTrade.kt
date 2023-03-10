/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.trade

import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.pokemon.Pokemon

class ClientTrade {
    var myOffer: Pokemon? = null
    var oppositeOffer = SettableObservable<Pokemon?>(null)
    var oppositeAcceptedMyOffer = SettableObservable(false)
    var acceptedOppositeOffer = false

    var completedEmitter = SimpleObservable<Unit>()
    var cancelEmitter = SimpleObservable<Unit>()

    fun accept() {
//        val acceptedId = oppositeOffer!!.uuid
//        CobblemonNetwork.sendToServer()
    }


}