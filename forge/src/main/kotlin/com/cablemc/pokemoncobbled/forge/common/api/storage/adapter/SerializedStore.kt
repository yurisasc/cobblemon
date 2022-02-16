package com.cablemc.pokemoncobbled.forge.common.api.storage.adapter

import com.cablemc.pokemoncobbled.forge.common.api.storage.PokemonStore
import java.util.UUID

data class SerializedStore<S>(val storeClass: Class<out PokemonStore<*>>, val uuid: UUID, val serializedForm: S)