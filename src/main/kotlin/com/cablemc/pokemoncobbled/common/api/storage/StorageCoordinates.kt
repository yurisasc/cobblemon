package com.cablemc.pokemoncobbled.common.api.storage

data class StorageCoordinates<T : StorePosition>(
    val store: PokemonStore<T>,
    val position: T
)