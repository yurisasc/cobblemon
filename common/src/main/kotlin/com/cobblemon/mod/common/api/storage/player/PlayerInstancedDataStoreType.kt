/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.storage.player

import com.cobblemon.mod.common.api.storage.player.client.ClientGeneralPlayerData
import com.cobblemon.mod.common.api.storage.player.client.ClientInstancedPlayerData
import com.cobblemon.mod.common.api.storage.player.client.ClientPokedexPlayerData
import com.cobblemon.mod.common.net.messages.client.starter.SetClientPlayerDataPacket
import net.minecraft.network.PacketByteBuf

/**
 * Basically, each type here has a server representation, and a client representation
 * Each type has it's own deserialization logic, and an action to run on the client after the object is deserialized
 */
enum class PlayerInstancedDataStoreType(
    val decoder: (PacketByteBuf) -> (SetClientPlayerDataPacket),
    val runAction: (ClientInstancedPlayerData) -> (Unit)
) {
    GENERAL(ClientGeneralPlayerData::decode, ClientGeneralPlayerData::runAction),
    POKEDEX(ClientPokedexPlayerData::decode, ClientPokedexPlayerData::runAction)
}