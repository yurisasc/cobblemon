/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.npc

import com.cobblemon.mod.common.api.npc.configuration.NPCBattleConfiguration
import net.minecraft.entity.EntityDimensions
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

/**
 * A class of NPC. This can contain a lot of preset information about the NPC's behaviour. Consider this the Pokémon
 * species but for NPCs.
 *
 * @author Hiroku
 * @since August 14th, 2023
 */
class NPCClass {
    @Transient
    lateinit var resourceIdentifier: Identifier

    var hitbox = EntityDimensions(0.6F, 1.8F, true)
    var battleConfiguration = NPCBattleConfiguration()


    fun encode(buffer: PacketByteBuf) {
        buffer.writeFloat(this.hitbox.width)
        buffer.writeFloat(this.hitbox.height)
        buffer.writeBoolean(this.hitbox.fixed)
        battleConfiguration.encode(buffer)
    }

    fun decode(buffer: PacketByteBuf) {
        hitbox = EntityDimensions(buffer.readFloat(), buffer.readFloat(), buffer.readBoolean())
        battleConfiguration = NPCBattleConfiguration()
        battleConfiguration.decode(buffer)
    }
}