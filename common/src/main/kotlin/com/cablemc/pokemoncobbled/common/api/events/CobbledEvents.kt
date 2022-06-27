package com.cablemc.pokemoncobbled.common.api.events

import com.cablemc.pokemoncobbled.common.api.events.entity.EntityAttributeEvent
import com.cablemc.pokemoncobbled.common.api.events.net.MessageBuiltEvent
import com.cablemc.pokemoncobbled.common.api.events.pokemon.FriendshipUpdateEvent
import com.cablemc.pokemoncobbled.common.api.events.pokemon.PokemonFaintedEvent
import com.cablemc.pokemoncobbled.common.api.events.pokemon.ShoulderMountEvent
import com.cablemc.pokemoncobbled.common.api.events.pokemon.evolution.EvolutionAcceptedEvent
import com.cablemc.pokemoncobbled.common.api.events.pokemon.evolution.EvolutionDisplayEvent
import com.cablemc.pokemoncobbled.common.api.events.pokemon.interaction.ExperienceCandyUseEvent
import com.cablemc.pokemoncobbled.common.api.reactive.CancelableObservable
import com.cablemc.pokemoncobbled.common.api.reactive.EventObservable
import com.cablemc.pokemoncobbled.common.util.asObservable
import com.cablemc.pokemoncobbled.common.util.asServerObservable
import com.cablemc.pokemoncobbled.common.util.asTickObservable
import dev.architectury.event.events.common.LifecycleEvent
import dev.architectury.event.events.common.PlayerEvent
import dev.architectury.event.events.common.PlayerEvent.PlayerJoin
import dev.architectury.event.events.common.PlayerEvent.PlayerQuit
import dev.architectury.event.events.common.TickEvent
import net.minecraft.server.network.ServerPlayerEntity

object CobbledEvents {
    val MESSAGE_BUILT = EventObservable<MessageBuiltEvent<*>>()
    val ENTITY_ATTRIBUTE = EventObservable<EntityAttributeEvent>()
    val SHOULDER_MOUNT = CancelableObservable<ShoulderMountEvent>()
    val FRIENDSHIP_UPDATED = EventObservable<FriendshipUpdateEvent>()
    val POKEMON_FAINTED = EventObservable<PokemonFaintedEvent>()
    val EVOLUTION_ACCEPTED = CancelableObservable<EvolutionAcceptedEvent>()
    val EVOLUTION_DISPLAY = EventObservable<EvolutionDisplayEvent>()
    val EXPERIENCE_CANDY_USE_PRE = CancelableObservable<ExperienceCandyUseEvent.Pre>()
    val EXPERIENCE_CANDY_USE_POST = EventObservable<ExperienceCandyUseEvent.Post>()

    val SERVER_STARTING = LifecycleEvent.SERVER_STARTING.asServerObservable()
    val SERVER_STOPPING = LifecycleEvent.SERVER_STOPPING.asServerObservable()

    val SERVER_STARTED = LifecycleEvent.SERVER_STARTED.asServerObservable()
    val SERVER_STOPPED = LifecycleEvent.SERVER_STOPPED.asServerObservable()

    val TICK_PRE = TickEvent.SERVER_PRE.asTickObservable()
    val TICK_POST = TickEvent.SERVER_POST.asTickObservable()

    val PLAYER_JOIN = PlayerEvent.PLAYER_JOIN.asObservable<PlayerJoin, ServerPlayerEntity> { obs -> PlayerJoin { obs.emit(it) } }
    val PLAYER_QUIT = PlayerEvent.PLAYER_QUIT.asObservable<PlayerQuit, ServerPlayerEntity> { obs -> PlayerQuit { obs.emit(it) } }
}