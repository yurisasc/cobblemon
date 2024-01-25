/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.riding.seats

import com.cobblemon.mod.common.api.riding.seats.properties.SeatDTO
import com.cobblemon.mod.common.api.riding.seats.properties.SeatProperties
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.MinecraftServer

/**
 * Represents a particular seat that is available to a pokemon capable of being ridden. The two main properties
 * of a seat are whether it's meant for the driver, and the offset applied to an entity sitting on the seat.
 *
 * Care should be taken when tracking the occupant attached to a seat. The implementation should verify the entity
 * is properly detached where necessary as to avoid a memory leak down the road.
 *
 * @property properties The respective properties that make up this seat, such as the position the entity will sit
 * when mounted on this seat.
 * @property occupant If occupied, represents the entity currently sitting in this seat.
 * @since 1.5.0
 */
data class Seat(
    val mount: PokemonEntity,
    val properties: SeatProperties,
    private var occupant: Entity? = null
) {

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
    fun acceptsRider(rider: Entity) : Boolean {
        if (this.occupied()) {
            return false
        }

        if (this.properties.driver) {
            if (rider is PlayerEntity) {
                return this.mount.pokemon.getOwnerUUID() == rider.uuid
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

    /**
     * Attempts to mount an entity onto the given pokemon mount, if specified. Where null, the
     * occupant will force a dismount instead. Otherwise, the requested rider will attempt to be
     * positioned onto this seat, such that it is a valid request. In other words, we would still
     * disallow attempts to sit the pokemon upon the pokemon.
     *
     * @param rider The entity that will ride on the pokemon using this seat
     * @param update Whether this call should inform the client that its set of seats has been updated
     * @since 1.5.0
     */
    @JvmOverloads
    fun mount(rider: Entity?, update: Boolean = true) : Boolean {
        if (!this.mount.world.isClient) {
            if (rider == null) {
                this.dismount(update)
                return true
            }

            if (rider.startRiding(mount)) {
                this.occupant = rider
                if (update) {
                    this.mount.dataTracker.set(PokemonEntity.SEAT_UPDATER, this.mount.riding.seats?.map { it.toDTO() } ?: emptyList())
                }

                return true
            }

            return false
        } else {
            this.occupant = rider
        }

        return true
    }

    /**
     * If a rider currently occupies this seat, they will be detached from the seat.
     *
     * @param update Whether this call should inform the client that its set of seats has been updated
     * @since 1.5.0
     */
    @JvmOverloads
    fun dismount(update: Boolean = true) {
        if (this.occupant != null) {
            this.occupant = null

            if (update) {
                this.mount.dataTracker.set(PokemonEntity.SEAT_UPDATER, this.mount.riding.seats?.map { it.toDTO() } ?: emptyList())
            }
        }
    }

    /**
     * Switches the occupants of two seats between each other, such that they belong to the same mount.
     *
     * @since 1.5.0
     */
    fun switch(target: Seat) : Boolean {
        if (target.mount.uuid == this.mount.uuid) {
            this.mount(target.occupant, false)
            target.mount(this.occupant, true)

            return true
        }

        return false
    }

    /**
     * Creates a data serializable version of a Seat that is to be used when providing updates from server
     * to client.
     *
     * @since 1.5.0
     */
    fun toDTO() : SeatDTO {
        return SeatDTO(this.properties, this.occupant)
    }

}