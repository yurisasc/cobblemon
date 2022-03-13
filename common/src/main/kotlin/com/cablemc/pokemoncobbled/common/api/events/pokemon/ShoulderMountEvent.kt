package com.cablemc.pokemoncobbled.common.api.events.pokemon

import com.cablemc.pokemoncobbled.common.api.events.Cancelable
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.server.level.ServerPlayer

/**
 * Event that is fired when a Player mounts a Pokemon to a shoulder
 *
 * @author Qu
 * @since 2022-01-26
 */
data class ShoulderMountEvent(
    val player: ServerPlayer,
    val pokemon: Pokemon,
    val isLeft: Boolean
) : Cancelable()