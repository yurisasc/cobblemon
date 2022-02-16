package com.cablemc.pokemoncobbled.common.api.event.pokemon

import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.eventbus.api.Cancelable
import net.minecraftforge.eventbus.api.Event

/**
 * Event that is fired when a Player mounts a Pokemon to a shoulder
 *
 * @author Qu
 * @since 2022-01-26
 */
@Cancelable
data class ShoulderMountEvent(
    val player: ServerPlayer,
    val pokemon: Pokemon,
    val isLeft: Boolean
) : Event()