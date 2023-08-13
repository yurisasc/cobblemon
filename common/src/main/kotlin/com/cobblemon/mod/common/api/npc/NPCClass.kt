package com.cobblemon.mod.common.api.npc

import net.minecraft.entity.EntityDimensions
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

/**
 * A class of NPC. This can contain a lot of preset information about the NPC's behaviour. Consider this the Pokémon
 * species but for NPCs.
 */
class NPCClass {
    @Transient
    lateinit var resourceIdentifier: Identifier

    var hitbox = EntityDimensions(0.6F, 1.8F, true)

    fun encode(buffer: PacketByteBuf) {
        buffer.writeFloat(this.hitbox.width)
        buffer.writeFloat(this.hitbox.height)
        buffer.writeBoolean(this.hitbox.fixed)
    }

    fun decode(buffer: PacketByteBuf) {
        hitbox = EntityDimensions(buffer.readFloat(), buffer.readFloat(), buffer.readBoolean())
    }
}