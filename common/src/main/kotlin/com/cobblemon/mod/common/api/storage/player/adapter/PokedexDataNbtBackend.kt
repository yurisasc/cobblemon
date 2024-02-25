package com.cobblemon.mod.common.api.storage.player.adapter

import com.cobblemon.mod.common.api.pokedex.Pokedex
import com.cobblemon.mod.common.api.storage.player.PlayerInstancedDataStoreType
import com.mojang.serialization.Codec
import java.util.UUID

class PokedexDataNbtBackend : NbtBackedPlayerData<Pokedex>("pokedex", PlayerInstancedDataStoreType.POKEDEX) {
    override val codec = Pokedex.CODEC
    override val defaultData = PokedexDataJsonBackend.defaultDataFunc

}