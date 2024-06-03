/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.riding

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d

data class RidingManager(val entity: PokemonEntity) {
    var lastSpeed = 0F

    /**
     * Responsible for handling riding conditions and transitions amongst controllers. This will tick
     * whenever the entity receives a tickControlled interaction.
     */
    fun tick(entity: PokemonEntity, driver: PlayerEntity, input: Vec3d) {
        val controller = entity.pokemon.riding.controllers.firstOrNull { it.condition.invoke(entity) } ?: return

        val poser = controller.poseProvider
        entity.dataTracker.set(PokemonEntity.POSE_TYPE, poser.select(entity))

        driver.sendMessage(Text.literal("Speed: ").styled { it.withColor(Formatting.GREEN) }.append(Text.literal("$lastSpeed b/t")), true)
    }

    fun speed(entity: PokemonEntity, driver: PlayerEntity): Float {
        val controller = entity.pokemon.riding.controllers.firstOrNull { it.condition.invoke(entity) }
        this.lastSpeed = controller?.speed(entity, driver) ?: 0.05F
        return this.lastSpeed
    }

    fun controlledRotation(entity: PokemonEntity, driver: PlayerEntity): Vec2f {
        val controller = entity.pokemon.riding.controllers.firstOrNull { it.condition.invoke(entity) }
        return controller?.rotation(driver) ?: Vec2f.ZERO
    }

    fun velocity(entity: PokemonEntity, driver: PlayerEntity, input: Vec3d): Vec3d {
        val controller = entity.pokemon.riding.controllers.firstOrNull { it.condition.invoke(entity) }
        return controller?.velocity(driver, input) ?: Vec3d.ZERO
    }

    fun canJump(entity: PokemonEntity, driver: PlayerEntity): Boolean {
        val controller = entity.pokemon.riding.controllers.firstOrNull { it.condition.invoke(entity) }
        return controller?.canJump(entity, driver) ?: false
    }

    fun jumpVelocity(entity: PokemonEntity, driver: PlayerEntity, jumpStrength: Int): Vec3d {
        val controller = entity.pokemon.riding.controllers.firstOrNull { it.condition.invoke(entity) }
        return controller?.jumpForce(entity, driver, jumpStrength) ?: Vec3d.ZERO
    }
}