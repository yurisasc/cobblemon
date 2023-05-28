package com.cobblemon.mod.common.api.riding

import com.cobblemon.mod.common.api.riding.properties.riding.RidingProperties
import com.cobblemon.mod.common.api.riding.seats.Seat
import net.minecraft.entity.JumpingMount

/**
 * Represents an entity that supports riding.
 *
 * @since 1.5.0
 */
interface Rideable : JumpingMount {

    /**
     * A set of properties denoting how a rideable entity is meant to behave under certain conditions
     *
     * @since 1.5.0
     */
    val properties: RidingProperties

    /**
     * Specifies a list of stateful seats which are capable of tracking an occupant.
     *
     * @since 1.5.0
     */
    val seats: List<Seat>

}