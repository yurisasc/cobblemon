/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.entity

import com.cablemc.pokemod.common.api.entity.EntitySideDelegate
import com.cablemc.pokemod.common.api.reactive.Observable.Companion.emitWhile
import com.cablemc.pokemod.common.client.render.models.blockbench.PoseableEntityState
import com.cablemc.pokemod.common.client.render.pokeball.animation.OpenAnimation
import com.cablemc.pokemod.common.client.render.pokeball.animation.ShakeAnimation
import com.cablemc.pokemod.common.entity.pokeball.EmptyPokeBallEntity
import com.cablemc.pokemod.common.entity.pokeball.EmptyPokeBallEntity.CaptureState
import com.cablemc.pokemod.common.entity.pokeball.EmptyPokeBallEntity.CaptureState.HIT
import com.cablemc.pokemod.common.entity.pokeball.EmptyPokeBallEntity.CaptureState.SHAKE

class EmptyPokeBallClientDelegate : PoseableEntityState<EmptyPokeBallEntity>(), EntitySideDelegate<EmptyPokeBallEntity> {
    override fun initialize(entity: EmptyPokeBallEntity) {
        entity.captureState.subscribe {
            when (CaptureState.values()[it.toInt()]) {
                HIT -> {
                    statefulAnimations.add(OpenAnimation())
                }
                CaptureState.FALL -> {
                    statefulAnimations.clear()
                }
                SHAKE -> {
                    entity.shakeEmitter
                        .pipe(emitWhile { entity.captureState.get() == SHAKE.ordinal.toByte() })
                        .subscribe { statefulAnimations.add(ShakeAnimation(0.8F)) }
                }
                else -> {}
            }
        }
    }
}