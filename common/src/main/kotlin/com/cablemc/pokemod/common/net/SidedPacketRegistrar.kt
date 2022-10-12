/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
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
        event.messageBuilder.registerHandler(handler)
    }

    protected inline fun <reified T : NetworkPacket> registerHandler(handler: PacketHandler<T>) {
        PokemodEvents.MESSAGE_BUILT
            .pipe(filter { it.clazz == T::class.java })
            .subscribe { register(it as MessageBuiltEvent<T>, handler) }
    }
}