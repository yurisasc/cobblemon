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
import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent
import com.cobblemon.mod.common.api.events.pokemon.TradeCompletedEvent
import com.cobblemon.mod.common.api.events.pokemon.evolution.EvolutionCompleteEvent
import com.cobblemon.mod.common.api.storage.player.PlayerInstancedDataStoreType
import com.cobblemon.mod.common.net.messages.client.SetClientPlayerDataPacket
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.getPlayer
import net.minecraft.server.MinecraftServer

object PokedexHandler {
    fun onCapture(event : PokemonCapturedEvent) {
        val pokedexData = Cobblemon.playerDataManager.getPokedexData(event.player)
        pokedexData.pokemonCaptured(event.pokemon)
        event.player.sendPacket(SetClientPlayerDataPacket(PlayerInstancedDataStoreType.POKEDEX, pokedexData.toClientData()))
    }

    fun onEvolve(event: EvolutionCompleteEvent){
        val ownedBy = event.pokemon.getOwnerPlayer()
        if(ownedBy == null){
            Cobblemon.LOGGER.warn("Evolved ${event.pokemon.species} that is not owned by any player. Stat was not tracked.")
            return
        }

        val pokedexData = Cobblemon.playerDataManager.getPokedexData(ownedBy)
        pokedexData.pokemonCaptured(event.pokemon)
        ownedBy.sendPacket(SetClientPlayerDataPacket(PlayerInstancedDataStoreType.POKEDEX, pokedexData.toClientData()))
    }

    fun onTrade(event: TradeCompletedEvent){
        var player = event.tradeParticipant1.uuid.getPlayer()
        if(player == null){
            Cobblemon.LOGGER.warn("Player with UUID '${event.tradeParticipant1.uuid} could not be found.")
        } else {
            val pokedexData = Cobblemon.playerDataManager.getPokedexData(player)
            pokedexData.pokemonCaptured(event.tradeParticipant1Pokemon)
            player.sendPacket(SetClientPlayerDataPacket(PlayerInstancedDataStoreType.POKEDEX, pokedexData.toClientData()))
        }

        player = event.tradeParticipant2.uuid.getPlayer()
        if(player == null){
            Cobblemon.LOGGER.warn("Player with UUID '${event.tradeParticipant2.uuid} could not be found.")
        } else {
            val pokedexData = Cobblemon.playerDataManager.getPokedexData(player)
            pokedexData.pokemonCaptured(event.tradeParticipant2Pokemon)
            player.sendPacket(SetClientPlayerDataPacket(PlayerInstancedDataStoreType.POKEDEX, pokedexData.toClientData()))
        }
    }

    fun onWildEncounter(pokemon : Pokemon){

    }
}