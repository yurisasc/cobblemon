/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.net

import com.cablemc.pokemod.common.api.events.PokemodEvents
import com.cablemc.pokemod.common.api.events.net.MessageBuiltEvent
import com.cablemc.pokemod.common.api.net.NetworkPacket
import com.cablemc.pokemod.common.api.reactive.Observable.Companion.filter
import com.cablemc.pokemod.common.client.net.ClientPacketHandler
import com.cablemc.pokemod.common.net.serverhandling.ServerPacketHandler

/**
 * Registers packet handlers for a particular side. It's a bit hellish because of a desire for generic type conformity
 * and some inanity in the way Forge built this rubbish.
 *
 * @author Hiroku
 * @since November 27th, 2021
 */
abstract class SidedPacketRegistrar {
    abstract fun registerHandlers()

    inline fun <reified T : NetworkPacket> register(event: MessageBuiltEvent<T>, handler: PacketHandler<T>) {
        if (handler is ClientPacketHandler && event.isToServer) {
            throw IllegalArgumentException("${handler::class.simpleName} is being registered on the client but the packet is to the server")
        }
        if (handler is ServerPacketHandler && !event.isToServer) {
            throw IllegalArgumentException("${handler::class.simpleName} is being registered on the server but the packet is to the client")
        }

        event.messageBuilder.registerHandler(handler)
    }

    var handlersRegistered = 0

    protected inline fun <reified T : NetworkPacket> registerHandler(handler: PacketHandler<T>) {
        handlersRegistered++
        PokemodEvents.MESSAGE_BUILT
            .pipe(filter { it.clazz == T::class.java })
            .subscribe { register(it as MessageBuiltEvent<T>, handler) }
    }
}