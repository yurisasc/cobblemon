package com.cobblemon.mod.common.api.riding.seats

import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.api.riding.seats.properties.SeatDTO
import com.cobblemon.mod.common.api.riding.seats.properties.SeatProperties
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf

/**
 * Represents a particular seat that is available to a pokemon capable of being ridden. The two main properties
 * of a seat are whether it's meant for the driver, and the offset applied to an entity sitting on the seat.
 *
 * Care should be taken when tracking the occupant attached to a seat. The implementation should verify the entity
 * is properly detached where necessary as to avoid a memory leak down the road.
 *
 * @property occupant If occupied, represents the entity currently sitting in this seat.
 * @since 1.5.0
 */
data class Seat(
    val properties: SeatProperties,
    private var occupant: Entity? = null
) : Encodable {

    /**
     * Specifies if any occupant currently occupies this seat. With the way rider lists are managed, we cannot
     * safely guarantee any seat represents a particular index of the list. The list not only scales to size, but
     * allows for shifting passenger position around. Think about boats, where a mob can occupy a position before the
     * player enters the boat. At that point, the player is forced into the driver seat and the other entity is forced
     * into the next index.
     *
     * For a seat with Cobblemon, our design will allow for set seat positions that are not bound to the list index
     * position, but simply track the entity sitting within the seat. This is meant to additionally allow a player
     * to switch seat positions without changing the list of riders.
     *
     * @return `true` if the seat is occupied, `false` otherwise
     * @since 1.5.0
     */
    fun occupied() : Boolean {
        return occupant != null
    }

    /**
     * Specifies whether, given the riding entity and the target mount, a rider is capable of mounting
     * the mount. The fail conditions for this are simply if a seat is occupied, or this particular seat
     * is considered the driver's seat and the rider is either not a player or does not own the pokemon.
     *
     * @return `true` if the rider is allowed to mount the entity, `false` otherwise
     * @since 1.5.0
     */
    fun acceptsRider(rider: Entity, mount: PokemonEntity) : Boolean {
        if(this.occupied()) {
            return false
        }

        if(this.properties.driver) {
            if(rider is PlayerEntity) {
                return mount.pokemon.getOwnerUUID() == rider.uuid
            }

            return false
        }

        return true
    }

    /**
     * Specifies the entity currently holding this seat, if any.
     *
     * @return The entity that is currently occupying this seat, if any
     * @since 1.5.0
     */
    fun occupant() : Entity? {
        return this.occupant
    }

    @JvmOverloads
    fun mount(mount: PokemonEntity, rider: Entity?, update: Boolean = true) {
        if(!mount.world.isClient) {
            if(rider == null) {
                this.dismount(mount)
                return
            }

            rider.yaw = mount.yaw
            rider.pitch = mount.pitch

            rider.startRiding(mount)

            this.occupant = rider
            if(update) {
                mount.seatUpdater.set(mount.seats.map { it.toDTO() })
            }
        } else {
            this.occupant = rider
        }
    }

    fun dismount(mount: PokemonEntity) {
        this.occupant = null
        mount.seatUpdater.set(mount.seats.map { it.toDTO() })
    }

    fun switch(mount: PokemonEntity, target: Seat) {
        this.mount(mount, target.occupant, false)
        target.mount(mount, this.occupant, true)
    }

    private fun toDTO() : SeatDTO {
        return SeatDTO(this.properties, this.occupant)
    }

    override fun encode(buffer: PacketByteBuf) {
        this.properties.encode(buffer)
        buffer.writeNullable(this.occupant) { _, occupant -> buffer.writeInt(occupant.id) }
    }

}