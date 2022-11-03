/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.events

import com.cablemc.pokemod.common.api.events.drops.LootDroppedEvent
import com.cablemc.pokemod.common.api.events.entity.EntityAttributeEvent
import com.cablemc.pokemod.common.api.events.net.MessageBuiltEvent
import com.cablemc.pokemod.common.api.events.pokemon.*
import com.cablemc.pokemod.common.api.events.pokemon.evolution.EvolutionAcceptedEvent
import com.cablemc.pokemod.common.api.events.pokemon.evolution.EvolutionCompleteEvent
import com.cablemc.pokemod.common.api.events.pokemon.evolution.EvolutionDisplayEvent
import com.cablemc.pokemod.common.api.events.pokemon.interaction.ExperienceCandyUseEvent
import com.cablemc.pokemod.common.api.events.starter.StarterChosenEvent
import com.cablemc.pokemod.common.api.reactive.CancelableObservable
import com.cablemc.pokemod.common.api.reactive.EventObservable
import com.cablemc.pokemod.common.util.asObservable
import com.cablemc.pokemod.common.util.asServerObservable
import com.cablemc.pokemod.common.util.asTickObservable
import dev.architectury.event.events.common.LifecycleEvent
import dev.architectury.event.events.common.PlayerEvent
import dev.architectury.event.events.common.PlayerEvent.PlayerJoin
import dev.architectury.event.events.common.PlayerEvent.PlayerQuit
import dev.architectury.event.events.common.TickEvent
import net.minecraft.server.network.ServerPlayerEntity

object PokemodEvents {
    val MESSAGE_BUILT = EventObservable<MessageBuiltEvent<*>>()
    val ENTITY_ATTRIBUTE = EventObservable<EntityAttributeEvent>()
    val SHOULDER_MOUNT = CancelableObservable<ShoulderMountEvent>()
    val FRIENDSHIP_UPDATED = EventObservable<FriendshipUpdatedEvent>()
    val POKEMON_FAINTED = EventObservable<PokemonFaintedEvent>()
    val EVOLUTION_ACCEPTED = CancelableObservable<EvolutionAcceptedEvent>()
    val EVOLUTION_DISPLAY = EventObservable<EvolutionDisplayEvent>()
    val EVOLUTION_COMPLETE = EventObservable<EvolutionCompleteEvent>()
    val POKEMON_CAPTURED = EventObservable<PokemonCapturedEvent>()
    val EGG_HATCH = EventObservable<HatchEggEvent>()

    val LEVEL_UP_EVENT = EventObservable<LevelUpEvent>()

    val EXPERIENCE_GAINED_EVENT_PRE = CancelableObservable<ExperienceGainedPreEvent>()
    val EXPERIENCE_GAINED_EVENT_POST = EventObservable<ExperienceGainedPostEvent>()
    val EXPERIENCE_CANDY_USE_PRE = CancelableObservable<ExperienceCandyUseEvent.Pre>()
    val EXPERIENCE_CANDY_USE_POST = EventObservable<ExperienceCandyUseEvent.Post>()

    val LOOT_DROPPED = CancelableObservable<LootDroppedEvent>()
    val STARTER_CHOSEN = CancelableObservable<StarterChosenEvent>()

    val SERVER_STARTING = LifecycleEvent.SERVER_STARTING.asServerObservable()
    val SERVER_STOPPING = LifecycleEvent.SERVER_STOPPING.asServerObservable()

    val SERVER_STARTED = LifecycleEvent.SERVER_STARTED.asServerObservable()
    val SERVER_STOPPED = LifecycleEvent.SERVER_STOPPED.asServerObservable()

    val TICK_PRE = TickEvent.SERVER_PRE.asTickObservable()
    val TICK_POST = TickEvent.SERVER_POST.asTickObservable()

    val PLAYER_JOIN = PlayerEvent.PLAYER_JOIN.asObservable<PlayerJoin, ServerPlayerEntity> { obs -> PlayerJoin { obs.emit(it) } }
    val PLAYER_QUIT = PlayerEvent.PLAYER_QUIT.asObservable<PlayerQuit, ServerPlayerEntity> { obs -> PlayerQuit { obs.emit(it) } }
}