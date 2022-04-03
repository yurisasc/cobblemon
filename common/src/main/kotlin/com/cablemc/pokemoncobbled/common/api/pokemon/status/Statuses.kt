package com.cablemc.pokemoncobbled.common.api.pokemon.status

import com.cablemc.pokemoncobbled.common.util.RandomPeriod
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.resources.ResourceLocation

/**
 * Main API point for Statuses
 * Get or register Statuses
 *
 * @author Deltric
 */
object Statuses {
    private val allStatuses = mutableListOf<Status>()

    val BURN = registerStatus(Status(name = cobbledResource("burn"), nonVolatile = true, duration = RandomPeriod(180, 300)))

    fun registerStatus(status: Status) : Status {
        allStatuses.add(status)
        return status
    }

    fun getStatus(name: ResourceLocation): Status? {
        return allStatuses.find { status -> status.name == name }
    }
}