package com.cablemc.pokemoncobbled.common.api.event.pokemon

import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.eventbus.api.Cancelable
import net.minecraftforge.eventbus.api.Event

/**
 * Event that is fired when a player owned Pokemon has its happiness changed
 *
 * @author Blue
 * @since 2022-02-08
 */
@Cancelable
data class HappinessUpdateEvent(
    val player: ServerPlayer,
    val pokemon: Pokemon,
    val oldHappiness: Int
) : Event()