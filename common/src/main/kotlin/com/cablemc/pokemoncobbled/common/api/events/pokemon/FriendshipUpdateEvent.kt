package com.cablemc.pokemoncobbled.common.api.events.pokemon

import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.server.level.ServerPlayer

/**
 * Event that is fired when a player owned Pokémon has its happiness changed
 *
 * @author Blue
 * @since 2022-02-08
 */
data class FriendshipUpdateEvent(
    val player: ServerPlayer,
    val pokemon: Pokemon,
    var newFriendship: Int
)