/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.events

import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent
import com.cobblemon.mod.common.api.events.drops.LootDroppedEvent
import com.cobblemon.mod.common.api.events.entity.PokemonEntityLoadEvent
import com.cobblemon.mod.common.api.events.entity.PokemonEntitySaveEvent
import com.cobblemon.mod.common.api.events.entity.PokemonEntitySaveToWorldEvent
import com.cobblemon.mod.common.api.events.pokemon.ExperienceGainedPostEvent
import com.cobblemon.mod.common.api.events.pokemon.ExperienceGainedPreEvent
import com.cobblemon.mod.common.api.events.pokemon.FriendshipUpdatedEvent
import com.cobblemon.mod.common.api.events.pokemon.LevelUpEvent
import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent
import com.cobblemon.mod.common.api.events.pokemon.PokemonFaintedEvent
import com.cobblemon.mod.common.api.events.pokemon.ShoulderMountEvent
import com.cobblemon.mod.common.api.events.pokemon.evolution.EvolutionAcceptedEvent
import com.cobblemon.mod.common.api.events.pokemon.evolution.EvolutionCompleteEvent
import com.cobblemon.mod.common.api.events.pokemon.evolution.EvolutionDisplayEvent
import com.cobblemon.mod.common.api.events.pokemon.interaction.ExperienceCandyUseEvent
import com.cobblemon.mod.common.api.events.starter.StarterChosenEvent
import com.cobblemon.mod.common.api.reactive.CancelableObservable
import com.cobblemon.mod.common.api.reactive.EventObservable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import net.minecraft.server.network.ServerPlayerEntity

object CobblemonEvents {

    val DATA_SYNCHRONIZED = SimpleObservable<ServerPlayerEntity>()
    val SHOULDER_MOUNT = CancelableObservable<ShoulderMountEvent>()
    val FRIENDSHIP_UPDATED = EventObservable<FriendshipUpdatedEvent>()
    val POKEMON_FAINTED = EventObservable<PokemonFaintedEvent>()
    val EVOLUTION_ACCEPTED = CancelableObservable<EvolutionAcceptedEvent>()
    val EVOLUTION_DISPLAY = EventObservable<EvolutionDisplayEvent>()
    val EVOLUTION_COMPLETE = EventObservable<EvolutionCompleteEvent>()

    val POKEMON_CAPTURED = EventObservable<PokemonCapturedEvent>()
//    val EGG_HATCH = EventObservable<HatchEggEvent>()
    val BATTLE_VICTORY = EventObservable<BattleVictoryEvent>()

    val LEVEL_UP_EVENT = EventObservable<LevelUpEvent>()

    val POKEMON_ENTITY_SAVE = EventObservable<PokemonEntitySaveEvent>()
    val POKEMON_ENTITY_LOAD = CancelableObservable<PokemonEntityLoadEvent>()
    val POKEMON_ENTITY_SAVE_TO_WORLD = CancelableObservable<PokemonEntitySaveToWorldEvent>()

    val EXPERIENCE_GAINED_EVENT_PRE = CancelableObservable<ExperienceGainedPreEvent>()
    val EXPERIENCE_GAINED_EVENT_POST = EventObservable<ExperienceGainedPostEvent>()
    val EXPERIENCE_CANDY_USE_PRE = CancelableObservable<ExperienceCandyUseEvent.Pre>()
    val EXPERIENCE_CANDY_USE_POST = EventObservable<ExperienceCandyUseEvent.Post>()

    val LOOT_DROPPED = CancelableObservable<LootDroppedEvent>()
    val STARTER_CHOSEN = CancelableObservable<StarterChosenEvent>()
}