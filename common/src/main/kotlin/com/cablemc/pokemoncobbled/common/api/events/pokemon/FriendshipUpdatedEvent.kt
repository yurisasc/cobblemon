package com.cablemc.pokemoncobbled.common.api.events.pokemon

import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

/**
 * Event that is fired when a player owned Pokémon has its happiness changed
 *
 * @author Blue
 * @since 2022-02-08
 */
data class FriendshipUpdatedEvent(
    val pokemon: Pokemon,
    var newFriendship: Int
)