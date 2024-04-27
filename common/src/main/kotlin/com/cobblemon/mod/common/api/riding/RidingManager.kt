/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.riding

import com.cobblemon.mod.common.api.riding.capability.RidingCapability
import com.cobblemon.mod.common.api.riding.context.RidingContext
import com.cobblemon.mod.common.api.riding.context.RidingContextBuilder
import com.cobblemon.mod.common.api.riding.controller.RideController
import com.cobblemon.mod.common.api.riding.seats.Seat
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d

data class RidingManager(val entity: () -> PokemonEntity) {

    /**
     * Specifies a list of stateful seats which are capable of tracking an occupant.
     *
     * @since 1.5.0
     */
    var context: RidingContext = RidingContext(RidingContextBuilder())

    // TODO - Seats should realistically be final and manipulated by seat index.
    // TODO - On the client, these seats are decoded, so setting the variable here
    // TODO - breaks if final
    var seats: List<Seat> = this.entity().pokemon.riding.seats.map { it.create(this.entity()) }
    private lateinit var capabilities: Map<RidingCapability, RideController>

    private var firstTick: Boolean = true

    /**
     * Responsible for handling riding conditions and transitions amongst controllers. This will tick
     * whenever the entity receives a tickControlled interaction.
     */
    fun tick(entity: PokemonEntity, driver: PlayerEntity, input: Vec3d) {
        if(this.firstTick) {
            this.capabilities = this.entity().pokemon.riding.capabilities.associateWith {
                RideController.controllers[it.properties.identifier]!!
            }

            firstTick = false
        }

        val capability = this.capabilities.keys.firstOrNull { it.condition.test(entity) }
        val controller = this.capabilities[capability] ?: return
        this.context = this.context.apply(capability?.properties ?: return)

        // TODO - We need to figure out a better method for this, as inputs could still be applied when traversal
        // TODO - is impossible (consider walls)
        entity.dataTracker.set(PokemonEntity.MOVING, input != Vec3d.ZERO)

        val poser = controller.poseProvider
        entity.dataTracker.set(PokemonEntity.POSE_TYPE, poser.select(entity))

        context.speed = if(input == Vec3d.ZERO) 0F else controller.speed(entity, driver, this.context)
        driver.sendMessage(Text.literal("Speed: ").styled { it.withColor(Formatting.GREEN) }.append(Text.literal("${context.speed} b/t")), true)
    }

    fun controlledRotation(entity: PokemonEntity, driver: PlayerEntity): Vec2f {
        if(!this::capabilities.isInitialized) {
            return Vec2f.ZERO
        }

        val capability = this.capabilities.keys.firstOrNull { it.condition.test(entity) }
        val controller = this.capabilities[capability] ?: return Vec2f.ZERO

        return controller.rotation(driver)
    }

    fun velocity(entity: PokemonEntity, driver: PlayerEntity, input: Vec3d): Vec3d {
        if(!this::capabilities.isInitialized) {
            return Vec3d.ZERO
        }

        val capability = this.capabilities.keys.firstOrNull { it.condition.test(entity) }
        val controller = this.capabilities[capability] ?: return Vec3d.ZERO

        return controller.velocity(driver, input)
    }
}