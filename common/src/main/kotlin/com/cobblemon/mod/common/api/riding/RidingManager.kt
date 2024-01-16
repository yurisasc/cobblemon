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
import com.cobblemon.mod.common.api.riding.controller.posing.PoseProvider
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.Vec3d

data class RidingManager(val properties: RidingProperties) {

    private val context: RidingContext = RidingContext()
    private val capabilities: List<RidingCapability> = mutableListOf()
    lateinit var capability: RidingCapability

    fun init(entity: PokemonEntity) {
        this.capability = this.capabilities.first { it.condition.test(entity) }
    }

    /**
     * Responsible for handling riding conditions and transitions amongst controllers. This will tick
     * whenever the entity receives a tickControlled interaction.
     */
    fun tick(entity: PokemonEntity, driver: PlayerEntity, input: Vec3d) {
//        val poser: PoseProvider = this.capability.controller.poseProvider
//        entity.poseType.set(poser.select(entity))


    }

    private fun canTransitionTo(mount: PokemonEntity, capability: RidingCapability): Boolean {
        if(this.capability == capability) {
            return false
        }

        return capability.condition.test(mount)
    }
}