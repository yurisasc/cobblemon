package com.cablemc.pokemoncobbled.common.api.events

import com.cablemc.pokemoncobbled.common.api.events.entity.EntityAttributeEvent
import com.cablemc.pokemoncobbled.common.api.events.net.MessageBuiltEvent
import com.cablemc.pokemoncobbled.common.api.events.pokemon.ShoulderMountEvent
import com.cablemc.pokemoncobbled.common.api.reactive.CancelableObservable
import com.cablemc.pokemoncobbled.common.api.reactive.EventObservable

object CobbledEvents {
    val MESSAGE_BUILT = EventObservable<MessageBuiltEvent<*>>()
    val ENTITY_ATTRIBUTE_EVENT = EventObservable<EntityAttributeEvent>()
    val SHOULDER_MOUNT = CancelableObservable<ShoulderMountEvent>()
}