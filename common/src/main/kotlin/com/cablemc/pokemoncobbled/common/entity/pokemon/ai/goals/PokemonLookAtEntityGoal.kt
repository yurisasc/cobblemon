/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.entity.pokemon.ai.goals

import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonBehaviourFlag
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.goal.LookAtEntityGoal

/**
 * An override of the [LookAtEntityGoal] so that Pokémon behaviours can be implemented.
 *
 * @author Hiroku
 * @since July 30th, 2022
 */
class PokemonLookAtEntityGoal(entity: PokemonEntity, targetType: Class<out LivingEntity>, range: Float) : LookAtEntityGoal(entity, targetType, range) {
    fun canLook() = (mob as PokemonEntity).behaviour.moving.canLook
    override fun canStart() = super.canStart() && canLook()
    override fun shouldContinue() = super.shouldContinue() && canLook()

    override fun start() {
        super.start()
        (mob as PokemonEntity).setBehaviourFlag(PokemonBehaviourFlag.LOOKING, true)
    }

    override fun stop() {
        super.stop()
        (mob as PokemonEntity).setBehaviourFlag(PokemonBehaviourFlag.LOOKING, false)
    }
}