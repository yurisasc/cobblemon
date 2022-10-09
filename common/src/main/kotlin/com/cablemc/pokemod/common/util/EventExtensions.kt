/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.util

import com.cablemc.pokemod.common.api.reactive.Observable
import com.cablemc.pokemod.common.api.reactive.SimpleObservable
import dev.architectury.event.Event
import dev.architectury.event.events.common.LifecycleEvent.ServerState
import dev.architectury.event.events.common.TickEvent
import net.minecraft.server.MinecraftServer

fun <T, E> Event<T>.asObservable(subscription: (observable: SimpleObservable<E>) -> T): SimpleObservable<E> {
    val observable = SimpleObservable<E>()
    this.register(subscription(observable))
    return observable
}

fun Event<ServerState>.asServerObservable(): Observable<MinecraftServer> {
    return this.asObservable { obs -> ServerState { obs.emit(it) } }
}

fun Event<TickEvent.Server>.asTickObservable(): Observable<MinecraftServer> {
    return this.asObservable { obs -> TickEvent.Server { obs.emit(it) } }
}