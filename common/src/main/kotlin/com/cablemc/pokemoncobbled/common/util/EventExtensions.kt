package com.cablemc.pokemoncobbled.common.util

import com.cablemc.pokemoncobbled.common.api.reactive.Observable
import com.cablemc.pokemoncobbled.common.api.reactive.SimpleObservable
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