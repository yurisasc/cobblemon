package com.cablemc.pokemoncobbled.common.api.pokemon.status

import com.cablemc.pokemoncobbled.common.pokemon.status.PersistentStatus
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.util.Identifier

/**
 * Main API point for Statuses
 * Get or register Statuses
 *
 * @author Deltric
 */
object Statuses {
    private val allStatuses = mutableListOf<Status>()

    val BURN = registerStatus(PersistentStatus(name = cobbledResource("burn"), defaultDuration = IntRange(180, 300)))

    fun <T: Status> registerStatus(status: T) : T {
        allStatuses.add(status)
        return status
    }

    fun getStatus(name: Identifier): Status? {
        return allStatuses.find { status -> status.name == name }
    }
}