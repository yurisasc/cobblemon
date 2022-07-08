package com.cablemc.pokemoncobbled.common.events

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.storage.party.PlayerPartyStore
import com.cablemc.pokemoncobbled.common.battles.BattleRegistry
import com.cablemc.pokemoncobbled.common.util.party
import net.minecraft.server.MinecraftServer

object ServerTickHandler {
    private var secondsTick: Int = 0

    fun onTick(server: MinecraftServer) {
        PokemonCobbled.bestSpawner.spawnerManagers.forEach { it.onServerTick() }
        BattleRegistry.tick()

        secondsTick++

        // look at that space ;)
        if (secondsTick == 20) {
            secondsTick = 0

            // Party tick
            for(player in server.playerManager.playerList) {
                val partyStore = player.party() as PlayerPartyStore
                partyStore.onSecondPassed(player)
            }
        }
    }
}