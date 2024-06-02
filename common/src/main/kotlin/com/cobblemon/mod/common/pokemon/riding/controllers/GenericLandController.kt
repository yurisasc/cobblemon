/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.riding.controllers

import com.bedrockk.molang.runtime.MoLangRuntime
import com.bedrockk.molang.runtime.value.DoubleValue
import com.cobblemon.mod.common.api.riding.controller.RideController
import com.cobblemon.mod.common.api.riding.controller.posing.PoseOption
import com.cobblemon.mod.common.api.riding.controller.posing.PoseProvider
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.util.asExpression
import com.cobblemon.mod.common.util.blockPositionsAsListRounded
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.getString
import com.cobblemon.mod.common.util.resolveFloat
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import net.minecraft.util.shape.VoxelShapes

class GenericLandController : RideController {
    companion object {
        val KEY: Identifier = cobblemonResource("land/generic")
    }

//    private var previousVelocity = Vec3d.ZERO

    var canJump = "true".asExpression()
        private set
    var jumpVector = listOf("0".asExpression(), "0.3".asExpression(), "0".asExpression())
        private set
    var speed = "1.0".asExpression()
        private set

    private var runtime = MoLangRuntime()

    @Transient
    private var initializedEntityId = -1

    override val key: Identifier = KEY
    override val poseProvider: PoseProvider = PoseProvider(PoseType.STAND).with(PoseOption(PoseType.WALK) { it.dataTracker.get(PokemonEntity.MOVING) })
    override val condition: (PokemonEntity) -> Boolean = { entity ->
        //Are there any blocks under the mon that aren't air or fluid
        //Cant just check one block since some mons may be more than one block big
        //This should be changed so that the any predicate is only ran on blocks under the mon
        VoxelShapes.cuboid(entity.boundingBox).blockPositionsAsListRounded().any {
            //Need to check other fluids
            if (entity.isTouchingWater || entity.isSubmergedInWater) {
                return@any false
            }
            //This might not actually work, depending on what the yPos actually is. yPos of the middle of the entity? the feet?
            if (it.y.toDouble() == (entity.pos.y)) {
                val blockState = entity.world.getBlockState(it.down())
                return@any !blockState.isAir && blockState.fluidState.isEmpty
            }
            true
        }
    }

    // temporary until the struct stuff is properly and explicitly added to PokemonEntity
    private fun attachEntity(entity: PokemonEntity) {
        if (initializedEntityId == entity.id) {
            return
        }
        initializedEntityId = entity.id

        runtime.environment.query.addFunction("entity") { entity.struct }
    }

    override fun speed(entity: PokemonEntity, driver: PlayerEntity): Float {
        attachEntity(entity)
        return runtime.resolveFloat(speed)
    }

    override fun rotation(driver: LivingEntity): Vec2f {
        return Vec2f(driver.pitch * 0.5f, driver.yaw)
    }

    override fun velocity(driver: PlayerEntity, input: Vec3d): Vec3d {
        val f = driver.sidewaysSpeed * 0.2f
        var g = driver.forwardSpeed
        if (g <= 0.0f) {
            g *= 0.25f
        }

        val velocity = Vec3d(f.toDouble(), 0.0, g.toDouble())

        return velocity
    }

    override fun canJump(entity: PokemonEntity, driver: PlayerEntity) = true

    override fun jumpForce(entity: PokemonEntity, driver: PlayerEntity, jumpStrength: Int): Vec3d {
        attachEntity(entity)
        runtime.environment.query.addFunction("jump_strength") { DoubleValue(jumpStrength.toDouble()) }
        val jumpVector = jumpVector.map { runtime.resolveFloat(it) }
        return Vec3d(jumpVector[0].toDouble(), jumpVector[1].toDouble(), jumpVector[2].toDouble())
    }

    override fun encode(buffer: PacketByteBuf) {
        super.encode(buffer)
        buffer.writeString(this.speed.getString())
        buffer.writeString(this.canJump.getString())
        buffer.writeString(this.jumpVector[0].getString())
        buffer.writeString(this.jumpVector[1].getString())
        buffer.writeString(this.jumpVector[2].getString())
    }

    override fun decode(buffer: PacketByteBuf) {
        this.speed = buffer.readString().asExpression()
        this.canJump = buffer.readString().asExpression()
        this.jumpVector = listOf(
            buffer.readString().asExpression(),
            buffer.readString().asExpression(),
            buffer.readString().asExpression()
        )
    }
}