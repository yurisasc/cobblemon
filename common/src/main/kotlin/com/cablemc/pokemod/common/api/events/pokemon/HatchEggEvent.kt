package com.cablemc.pokemod.common.api.events.pokemon

import com.cablemc.pokemod.common.pokemon.Pokemon
import net.minecraft.server.network.ServerPlayerEntity

data class HatchEggEvent (
    val pokemon: Pokemon,
    val player: ServerPlayerEntity
)