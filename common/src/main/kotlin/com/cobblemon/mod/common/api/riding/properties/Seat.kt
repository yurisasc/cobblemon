package com.cobblemon.mod.common.api.riding.properties

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.math.Vec3d

data class Seat(val offset: Vec3d) {

    private var index: Int = 1

    fun occupied(entity: PokemonEntity) : Boolean {
        return entity.passengerList.size > index && entity.passengerList[index] != null
    }

    fun occupant(entity: PokemonEntity) : Entity? {
        if(this.occupied(entity)) {
            return entity.passengerList[index]
        }

        return null
    }

    fun attach(mount: PokemonEntity, rider: LivingEntity) {
        if(!mount.world.isClient) {
            rider.yaw = mount.yaw
            rider.pitch = mount.pitch

            rider.startRiding(mount)
        }
    }

    fun detach(entity: PokemonEntity) {

    }

    fun encode(buffer: PacketByteBuf) {
        buffer.writeDouble(offset.x)
        buffer.writeDouble(offset.y)
        buffer.writeDouble(offset.z)
    }

    companion object {

        fun decode(buffer: PacketByteBuf) : Seat {
            return Seat(Vec3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()))
        }

    }

}