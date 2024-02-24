/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.events

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.CobblemonNetwork.sendPacket
import com.cobblemon.mod.common.api.events.battles.BattleStartedPostEvent
import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent
import com.cobblemon.mod.common.api.events.pokemon.TradeCompletedEvent
import com.cobblemon.mod.common.api.events.pokemon.evolution.EvolutionCompleteEvent
import com.cobblemon.mod.common.api.storage.player.PlayerInstancedDataStoreType
import com.cobblemon.mod.common.api.storage.player.adapter.PokedexDataJsonBackend
import com.cobblemon.mod.common.battles.actor.PokemonBattleActor
import com.cobblemon.mod.common.net.messages.client.SetClientPlayerDataPacket
import com.cobblemon.mod.common.util.getPlayer

object PokedexHandler {
    fun onCapture(event : PokemonCapturedEvent) {
        val pokedexData = Cobblemon.playerDataManager.getPokedexData(event.player)
        pokedexData.pokemonCaught(event)
        event.player.sendPacket(SetClientPlayerDataPacket(PlayerInstancedDataStoreType.POKEDEX, pokedexData.toClientData(), false))
    }
    /** Commented out until incorporated into the new system

    fun onEvolve(event: EvolutionCompleteEvent){
        val ownedBy = event.pokemon.getOwnerPlayer()
        if(ownedBy == null){
            Cobblemon.LOGGER.warn("Evolved ${event.pokemon.species} that is not owned by any player. Stat was not tracked.")
            return
        }

        val pokedexData = Cobblemon.playerDataManager.getPokedexData(ownedBy)
        val incrementalPokedex = PokedexDataJsonBackend.defaultDataFunc.invoke(ownedBy.uuid)
        pokedexData.pokemonCaptured(event.pokemon)
        incrementalPokedex.pokemonCaptured(event.pokemon)
        ownedBy.sendPacket(SetClientPlayerDataPacket(PlayerInstancedDataStoreType.POKEDEX, pokedexData.getInstancedPiece(event.pokemon).toClientData(), true))
    }

    fun onTrade(event: TradeCompletedEvent){
        var player = event.tradeParticipant1.uuid.getPlayer()
        if(player == null){
            Cobblemon.LOGGER.warn("Player with UUID '${event.tradeParticipant1.uuid} could not be found.")
        } else {
            val pokedexData = Cobblemon.playerDataManager.getPokedexData(player)
            pokedexData.pokemonCaptured(event.tradeParticipant1Pokemon)
            player.sendPacket(SetClientPlayerDataPacket(PlayerInstancedDataStoreType.POKEDEX, pokedexData.getInstancedPiece(event.tradeParticipant1Pokemon).toClientData(), true))
        }

        player = event.tradeParticipant2.uuid.getPlayer()
        if(player == null){
            Cobblemon.LOGGER.warn("Player with UUID '${event.tradeParticipant2.uuid} could not be found.")
        } else {
            val pokedexData = Cobblemon.playerDataManager.getPokedexData(player)
            pokedexData.pokemonCaptured(event.tradeParticipant2Pokemon)
            player.sendPacket(SetClientPlayerDataPacket(PlayerInstancedDataStoreType.POKEDEX, pokedexData.getInstancedPiece(event.tradeParticipant2Pokemon).toClientData(), true))
        }
    }

    fun onBattleStart(event: BattleStartedPostEvent){
        if (event.battle.isPvW) {
            val player = event.battle.players.first()
            val wildActor = event.battle.actors.firstOrNull {it is PokemonBattleActor} as? PokemonBattleActor ?: return
            val wildPokemon = wildActor.pokemon.originalPokemon
            val pokedex = Cobblemon.playerDataManager.getPokedexData(player)
            pokedex.wildPokemonEncountered(wildPokemon)
            player.sendPacket(SetClientPlayerDataPacket(PlayerInstancedDataStoreType.POKEDEX, pokedex.getInstancedPiece(wildPokemon).toClientData(), true))
        }
    }
    **/
}