package com.cablemc.pokemoncobbled.common.api.starter

import com.cablemc.pokemoncobbled.common.config.starter.StarterCategory
import net.minecraft.server.network.ServerPlayerEntity

interface StarterHandler {
    fun getStarterList(player: ServerPlayerEntity): List<StarterCategory>
    fun handleJoin(player: ServerPlayerEntity)
    fun requestStarterChoice(player: ServerPlayerEntity)
    fun chooseStarter(player: ServerPlayerEntity, categoryName: String, index: Int)
}