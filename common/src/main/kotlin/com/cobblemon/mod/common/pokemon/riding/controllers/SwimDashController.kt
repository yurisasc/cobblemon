/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.riding.controllers

import com.cobblemon.mod.common.api.riding.controller.RideController
import com.cobblemon.mod.common.api.riding.controller.posing.PoseOption
import com.cobblemon.mod.common.api.riding.controller.posing.PoseProvider
import com.cobblemon.mod.common.api.riding.controller.properties.RideControllerPropertyKey
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.util.blockPositionsAsListRounded
import com.cobblemon.mod.common.util.cobblemonResource
import com.google.gson.JsonElement
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import net.minecraft.util.shape.VoxelShapes

data class SwimDashController(val dashSpeed: Float) : RideController {

    companion object {
        val KEY: Identifier = cobblemonResource("swim/dash")
        const val DASH_TICKS: Int = 60

        val DASH_SPEED: RideControllerPropertyKey<Float> = RideControllerPropertyKey(this.KEY)
    }

    override val key: Identifier = KEY
    override val poseProvider: PoseProvider = PoseProvider(PoseType.FLOAT)
        .with(PoseOption(PoseType.SWIM) { it.isSwimming && it.dataTracker.get(PokemonEntity.MOVING) })
    override val condition: (PokemonEntity) -> Boolean = { entity ->
        //This could be kinda weird... what if the top of the mon is in a fluid but the bottom isnt?
        VoxelShapes.cuboid(entity.boundingBox).blockPositionsAsListRounded().any {
            if (entity.isTouchingWater || entity.isSubmergedInWater) {
                return@any true
            }
            val blockState = entity.world.getBlockState(it)
            return@any !blockState.fluidState.isEmpty
        }
    }

    /** Indicates that we are currently enacting a dash, and that further movement inputs should be ignored */
    private var dashing = false
    private var ticks = 0

    override fun speed(entity: PokemonEntity, driver: PlayerEntity): Float {
        if(this.dashing) {
            if(this.ticks++ >= DASH_TICKS) {
                this.dashing = false
            }

            return 0.0F
        }

        this.dashing = true
        return this.dashSpeed
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

    override fun encode(buffer: PacketByteBuf) {
        super.encode(buffer)
        buffer.writeFloat(this.dashSpeed)
    }
}

object SwimDashControllerDeserializer : RideController.Deserializer {

    override fun deserialize(json: JsonElement): RideController {
        val obj = json.asJsonObject
        return SwimDashController(obj.get("dashSpeed").asFloat)
    }

    override fun decode(buffer: PacketByteBuf): RideController {
        return SwimDashController(buffer.readFloat())
    }

}
