/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.entity

import com.cobblemon.mod.common.api.entity.EntitySideDelegate
import com.cobblemon.mod.common.api.reactive.Observable.Companion.emitWhile
import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.pokeball.animation.OpenAnimation
import com.cobblemon.mod.common.client.render.pokeball.animation.ShakeAnimation
import com.cobblemon.mod.common.entity.pokeball.EmptyPokeBallEntity
import com.cobblemon.mod.common.entity.pokeball.EmptyPokeBallEntity.CaptureState
import com.cobblemon.mod.common.entity.pokeball.EmptyPokeBallEntity.CaptureState.HIT
import com.cobblemon.mod.common.entity.pokeball.EmptyPokeBallEntity.CaptureState.SHAKE
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