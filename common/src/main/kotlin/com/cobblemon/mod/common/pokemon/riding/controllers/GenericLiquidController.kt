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
import com.cobblemon.mod.common.api.riding.controller.properties.Deserializer
import com.cobblemon.mod.common.api.riding.controller.properties.RideControllerProperties
import com.cobblemon.mod.common.api.riding.controller.properties.RideControllerPropertyKeys
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.util.cobblemonResource
import com.google.gson.JsonElement
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import java.util.function.Predicate
import kotlin.math.max
import kotlin.math.min

object GenericLiquidController : RideController {
    override val key: Identifier = cobblemonResource("swim")
    override val poseProvider: PoseProvider = PoseProvider(PoseType.FLOAT)
        .with(PoseOption(PoseType.SWIM) { it.isSwimming && it.dataTracker.get(PokemonEntity.MOVING) })
    override val condition: (PokemonEntity) -> Boolean
        get() = TODO("Not yet implemented")

    override fun speed(entity: PokemonEntity, driver: PlayerEntity, context: RidingContext): Float {
        val max: Float = context.propertyOrDefault(RideControllerPropertyKeys.SPEED, 0.0F)
        val acceleration = this.acceleration(max, context)

        return min(max(context.speed + acceleration, 0.0F), 1.0F)
    }

    private fun acceleration(speed: Float, context: RidingContext): Float {
        val acceleration = context.propertyOrDefault(RideControllerPropertyKeys.ACCELERATION, 0.0F)
        return (1 / ((300 * speed) + (18.5F - (acceleration * 5.3F)))) * (0.9F * ((acceleration + 1) / 2))
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

}

data class GenericLiquidControllerProperties(
    val speed: Float,
    val acceleration: Float
) : RideControllerProperties {

    override val identifier: Identifier = GenericLiquidController.key

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeFloat(this.speed)
        buffer.writeFloat(this.acceleration)
    }

    override fun apply(context: RidingContextBuilder) {
        context.property(RideControllerPropertyKeys.SPEED, this.speed)
        context.property(RideControllerPropertyKeys.ACCELERATION, this.acceleration)
    }
}

object GenericLiquidControllerAdapter : Deserializer<GenericLiquidControllerProperties> {

    override fun deserialize(json: JsonElement): GenericLiquidControllerProperties {
        val obj = json.asJsonObject
        return GenericLiquidControllerProperties(obj.get("speed").asFloat, obj.get("acceleration").asFloat)
    }

    override fun decode(buffer: PacketByteBuf): GenericLiquidControllerProperties {
        return GenericLiquidControllerProperties(buffer.readFloat(), buffer.readFloat())
    }

}