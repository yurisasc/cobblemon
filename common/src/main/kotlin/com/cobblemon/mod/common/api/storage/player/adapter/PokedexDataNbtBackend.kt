/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.storage.player.adapter

import com.cobblemon.mod.common.api.pokedex.Pokedex
import com.cobblemon.mod.common.api.storage.player.PlayerInstancedDataStoreType
import com.mojang.serialization.Codec
import java.util.UUID

class PokedexDataNbtBackend : NbtBackedPlayerData<Pokedex>("pokedex", PlayerInstancedDataStoreType.POKEDEX) {
    override val codec = Pokedex.CODEC
    override val defaultData = PokedexDataJsonBackend.defaultDataFunc

}