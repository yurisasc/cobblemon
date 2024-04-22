package com.cobblemon.mod.common.api.events

import com.cobblemon.mod.common.api.events.client.ClientSendoutEvent
import com.cobblemon.mod.common.api.reactive.CancelableObservable
import com.cobblemon.mod.common.api.reactive.SimpleObservable

@Suppress("unused")
object CobblemonClientEvents {
    @JvmField
    val CLIENT_SENDOUT = SimpleObservable<ClientSendoutEvent>()
}