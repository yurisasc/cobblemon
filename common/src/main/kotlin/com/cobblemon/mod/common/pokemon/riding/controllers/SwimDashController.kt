/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.riding.controllers

import com.cobblemon.mod.common.api.riding.context.RidingContext
import com.cobblemon.mod.common.api.riding.context.RidingContextBuilder
import com.cobblemon.mod.common.api.riding.controller.RideController
import com.cobblemon.mod.common.api.riding.controller.posing.PoseOption
import com.cobblemon.mod.common.api.riding.controller.posing.PoseProvider
import com.cobblemon.mod.common.api.riding.controller.properties.RideControllerProperties
import com.cobblemon.mod.common.api.riding.controller.properties.RideControllerPropertyKey
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d

data class SwimDashController(val properties: SwimDashProperties) : RideController {

    override val key: Identifier = KEY
    override val poseProvider: PoseProvider = PoseProvider(PoseType.FLOAT)
        .with(PoseOption(PoseType.SWIM) { it.isSwimming && it.dataTracker.get(PokemonEntity.MOVING) })
    override val condition: (PokemonEntity) -> Boolean = { false }

    /** Indicates that we are currently enacting a dash, and that further movement inputs should be ignored */
    private var dashing = false
    private var ticks = 0

    companion object {
        val KEY: Identifier = cobblemonResource("swim/dash")
        const val DASH_TICKS: Int = 60
    }

    override fun speed(entity: PokemonEntity, driver: PlayerEntity, context: RidingContext): Float {
        if(this.dashing) {
            if(this.ticks++ >= DASH_TICKS) {
                this.dashing = false
            }

            return 0.0F
        }

        this.dashing = true
        return context.propertyOrDefault(SwimDashProperties.DASH_SPEED, 0.1F)
    }

    override fun rotation(driver: LivingEntity): Vec2f {
        return Vec2f(driver.pitch * 0.5f, driver.yaw)
    }

    override fun velocity(driver: PlayerEntity, input: Vec3d): Vec3d {
        val f = driver.sidewaysSpeed * 0.05f
        var g = driver.forwardSpeed * 0.6f
        if (g <= 0.0f) {
            g *= 0.25f
        }

        return Vec3d(f.toDouble(), 0.0, g.toDouble())
    }
}

data class SwimDashProperties(
    val dashSpeed: Float,
) : RideControllerProperties {

    override val identifier: Identifier = SwimDashController.KEY

    companion object {
        val DASH_SPEED: RideControllerPropertyKey<Float> = RideControllerPropertyKey(SwimDashController.KEY)
    }

    override fun apply(context: RidingContextBuilder) {
        context.property(DASH_SPEED, this.dashSpeed)
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeFloat(this.dashSpeed)
    }
}