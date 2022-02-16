package com.cablemc.pokemoncobbled.forge.common.net.serializers

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.syncher.EntityDataSerializer
import net.minecraft.world.phys.Vec3

object Vec3DataSerializer : EntityDataSerializer<Vec3> {
    override fun write(buffer: FriendlyByteBuf, vec: Vec3) {
        buffer.writeDouble(vec.x)
        buffer.writeDouble(vec.y)
        buffer.writeDouble(vec.z)
    }

    override fun read(buffer: FriendlyByteBuf) = Vec3(
        buffer.readDouble(),
        buffer.readDouble(),
        buffer.readDouble()
    )

    override fun copy(vec: Vec3) = Vec3(vec.x, vec.y, vec.z)
}