/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.entity.pokemon.ai.goals

import com.cablemc.pokemod.common.entity.pokemon.PokemonEntity
import net.minecraft.entity.ai.goal.WanderAroundGoal
import net.minecraft.tag.FluidTags
import net.minecraft.util.math.Vec3d

/**
 * An override of the [WanderAroundGoal] so that Pokémon behaviours can be implemented.
 *
 * @author Hiroku
 * @since July 30th, 2022
 */
class PokemonWanderAroundGoal(entity: PokemonEntity, speed: Double) : WanderAroundGoal(entity, speed) {
    fun canMove() = (mob as PokemonEntity).behaviour.moving.let { it.walk.canWalk || it.fly.canFly || (it.swim.canSwimInWater && mob.isSubmergedIn(FluidTags.WATER)) }
    override fun canStart() = super.canStart() && canMove() && !(mob as PokemonEntity).isBusy
    override fun shouldContinue() = super.shouldContinue() && canMove() && !(mob as PokemonEntity).isBusy

    override fun getWanderTarget(): Vec3d? {
        return super.getWanderTarget()?.add(0.0, 0.0, 0.0)
    }
}