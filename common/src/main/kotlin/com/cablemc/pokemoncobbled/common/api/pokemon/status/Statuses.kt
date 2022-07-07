package com.cablemc.pokemoncobbled.common.api.pokemon.status

import com.cablemc.pokemoncobbled.common.pokemon.status.statuses.Paralysis
import com.cablemc.pokemoncobbled.common.pokemon.status.statuses.Poison
import net.minecraft.util.Identifier

/**
 * Main API point for Statuses
 * Get or register Statuses
 *
 * @author Deltric
 */
object Statuses {
    private val allStatuses = mutableListOf<Status>()

    val POISON = registerStatus(Poison())
    val PARALYSIS = registerStatus(Paralysis())

    fun <T: Status> registerStatus(status: T) : T {
        allStatuses.add(status)
        return status
    }

    fun getStatus(name: Identifier): Status? {
        return allStatuses.find { status -> status.name == name }
    }
}