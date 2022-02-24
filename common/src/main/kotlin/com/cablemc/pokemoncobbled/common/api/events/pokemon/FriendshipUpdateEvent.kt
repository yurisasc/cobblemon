package com.cablemc.pokemoncobbled.common.api.event.pokemon

import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.eventbus.api.Event

/**
 * Event that is fired when a player owned Pokemon has its happiness changed
 *
 * @author Blue
 * @since 2022-02-08
 */

data class FriendshipUpdateEvent(
    val player: ServerPlayer,
    val pokemon: Pokemon,
    val newFriendship: Int
) : Event()