/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.entity.pokemon.ai.goals

import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import net.minecraft.entity.ai.goal.WanderAroundGoal
import net.minecraft.util.math.Vec3d

/**
 * An override of the [WanderAroundGoal] so that Pokémon behaviours can be implemented.
 *
 * @author Hiroku
 * @since July 30th, 2022
 */
class PokemonWanderAroundGoal(entity: PokemonEntity, speed: Double) : WanderAroundGoal(entity, speed) {
    fun canMove() = (mob as PokemonEntity).behaviour.moving.walk.canWalk
    override fun canStart() = super.canStart() && canMove()
    override fun shouldContinue() = super.shouldContinue() && canMove()

    override fun getWanderTarget(): Vec3d? {
        return super.getWanderTarget()?.add(0.0, 0.0, 0.0)
    }
}