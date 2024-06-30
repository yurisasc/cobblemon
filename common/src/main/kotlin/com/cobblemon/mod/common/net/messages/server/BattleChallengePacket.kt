/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.server

import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.util.cobblemonResource
import java.util.UUID
import net.minecraft.network.RegistryByteBuf

class BattleChallengePacket(val targetedEntityId: Int, val selectedPokemonId: UUID, val battleType: String = "singles") : NetworkPacket<BattleChallengePacket> {
    override val id = ID
    override fun encode(buffer: RegistryByteBuf) {
        buffer.writeInt(this.targetedEntityId)
        buffer.writeUuid(this.selectedPokemonId)
        buffer.writeString(battleType) //TODO: Probably need to make this in actual battleformat object
    }
    companion object {
        val ID = cobblemonResource("battle_challenge")
        fun decode(buffer: RegistryByteBuf) = BattleChallengePacket(buffer.readInt(), buffer.readUuid(), buffer.readString())
    }
}