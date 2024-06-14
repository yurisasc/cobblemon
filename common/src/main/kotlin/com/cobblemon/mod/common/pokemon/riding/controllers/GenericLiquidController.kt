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
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.util.blockPositionsAsListRounded
import com.cobblemon.mod.common.util.cobblemonResource
import kotlin.math.max
import kotlin.math.min
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.RegistryByteBuf
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import net.minecraft.util.shape.VoxelShapes

class GenericLiquidController : RideController {
    companion object {
        val KEY: Identifier = cobblemonResource("swim/generic")
    }

    var speed = 1F
        private set
    var acceleration = 1F
        private set

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

    override fun speed(entity: PokemonEntity, driver: PlayerEntity): Float {
        return min(max(this.speed + this.acceleration(), 0.0F), 1.0F)
    }

    private fun acceleration(): Float {
        return (1 / ((300 * this.speed) + (18.5F - (this.acceleration * 5.3F)))) * (0.9F * ((this.acceleration + 1) / 2))
    }

    override fun rotation(driver: LivingEntity): Vec2f {
        return Vec2f(driver.pitch * 0.5f, driver.yaw)
    }

    override fun velocity(driver: PlayerEntity, input: Vec3d): Vec3d {
        val f = driver.sidewaysSpeed * 0.1f
        var g = driver.forwardSpeed * 0.3f
        if (g <= 0.0f) {
            g *= 0.12f
        }

        return Vec3d(f.toDouble(), 0.0, g.toDouble())
    }

    override fun canJump(entity: PokemonEntity, driver: PlayerEntity): Boolean {
        TODO("Not yet implemented")
    }

    override fun jumpForce(entity: PokemonEntity, driver: PlayerEntity, jumpStrength: Int): Vec3d {
        TODO("Not yet implemented")
    }

    override fun encode(buffer: RegistryByteBuf) {
        super.encode(buffer)
        buffer.writeFloat(this.speed)
        buffer.writeFloat(this.acceleration)
    }

    override fun decode(buffer: RegistryByteBuf) {
        this.speed = buffer.readFloat()
        this.acceleration = buffer.readFloat()
    }
}