/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.riding.controllers

import com.cobblemon.mod.common.api.reference.Reference
import com.cobblemon.mod.common.api.riding.context.RidingContext
import com.cobblemon.mod.common.api.riding.context.state.RidingStateKeys
import com.cobblemon.mod.common.api.riding.controller.Deserializer
import com.cobblemon.mod.common.api.riding.controller.posing.PoseOption
import com.cobblemon.mod.common.api.riding.controller.posing.PoseProvider
import com.cobblemon.mod.common.api.riding.controller.RideController
import com.cobblemon.mod.common.api.riding.controller.properties.RideControllerProperties
import com.cobblemon.mod.common.api.riding.controller.properties.RideControllerPropertyKey
import com.cobblemon.mod.common.api.riding.controller.properties.RideControllerPropertyKeys
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.util.cobblemonResource
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import java.lang.reflect.Type
import java.util.function.Predicate

object GenericLandController : RideController {

    override val key: Identifier = cobblemonResource("land_generic")
    override val poseProvider: PoseProvider = PoseProvider(PoseType.STAND).with(PoseOption(PoseType.WALK) { it.isMoving.get() })
    override val conditions: Predicate<PokemonEntity> = Predicate<PokemonEntity> { it.isOnGround }

    override fun speed(entity: PokemonEntity, driver: PlayerEntity, context: RidingContext): Float {
        val max: Float = context.propertyOrDefault(RideControllerPropertyKeys.SPEED, 0.0F)
        val acceleration = this.acceleration(max, context)

        return context.stateOrDefault(RidingStateKeys.CURRENT_SPEED, 0.0F) + acceleration
    }

    private fun acceleration(speed: Float, context: RidingContext): Float {
        val acceleration = context.propertyOrDefault(RideControllerPropertyKeys.ACCELERATION, 0.0F)
        return (1 / ((300 * speed) + (18.5F - (acceleration * 5.3F)))) * (0.9F * ((acceleration + 1) / 2))
    }

    override fun rotation(driver: LivingEntity): Vec2f {
        return Vec2f(driver.pitch * 0.5f, driver.yaw)
    }

    override fun velocity(driver: PlayerEntity, input: Vec3d): Vec3d {
        val f = driver.sidewaysSpeed * 0.2f
        var g = driver.forwardSpeed * 0.5f
        if (g <= 0.0f) {
            g *= 0.25f
        }

        return Vec3d(f.toDouble(), 0.0, g.toDouble())
    }

}

data class GenericLandControllerProperties(
    var speed: Float,
    var acceleration: Float
) : RideControllerProperties() {

    override var identifier: Identifier = GenericLandController.key
    override fun toAccessibleProperties(): Map<RideControllerPropertyKey<*>, Reference<*>> {
        val result: MutableMap<RideControllerPropertyKey<*>, Reference<*>> = mutableMapOf()

        result[RideControllerPropertyKeys.SPEED] = Reference(this.speed)
        result[RideControllerPropertyKeys.ACCELERATION] = Reference(this.acceleration)
        return result
    }

    override fun encode(buffer: PacketByteBuf) {
        super.encode(buffer)
        buffer.writeFloat(this.speed)
        buffer.writeFloat(this.acceleration)
    }

    override fun decode(buffer: PacketByteBuf) {
        super.decode(buffer)
        this.speed = buffer.readFloat()
        this.acceleration = buffer.readFloat()
    }
}

object GenericLandControllerAdapter : Deserializer<GenericLandControllerProperties> {

    override fun deserialize(json: JsonElement): GenericLandControllerProperties {
        val obj = json.asJsonObject
        return GenericLandControllerProperties(obj.get("speed").asFloat, obj.get("acceleration").asFloat)
    }

}