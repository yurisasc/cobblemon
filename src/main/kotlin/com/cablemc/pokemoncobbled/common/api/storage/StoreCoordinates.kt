package com.cablemc.pokemoncobbled.common.api.storage

data class StoreCoordinates<T : StorePosition>(
    val store: PokemonStore<T>,
    val position: T
)