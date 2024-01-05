/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.pokeball

import com.cobblemon.mod.common.api.reactive.Observable
import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.cobblemon.mod.common.api.scheduling.Schedulable
import com.cobblemon.mod.common.client.render.models.blockbench.PosableState
import com.cobblemon.mod.common.client.render.models.blockbench.pokeball.AncientPokeBallModel
import com.cobblemon.mod.common.client.render.models.blockbench.pokeball.PokeBallModel
import com.cobblemon.mod.common.client.render.models.blockbench.repository.RenderContext
import com.cobblemon.mod.common.entity.pokeball.EmptyPokeBallEntity
import kotlin.random.Random

@Suppress("NAME_SHADOWING")
abstract class PokeBallPoseableState : PosableState(), Schedulable {
    abstract val stateEmitter: SettableObservable<EmptyPokeBallEntity.CaptureState>
    abstract val shakeEmitter: Observable<Unit>
    private val group = if (this.currentModel is AncientPokeBallModel) "ancient_poke_ball" else "poke_ball"

    open fun initSubscriptions() {
        stateEmitter.subscribe { state ->
            when (state) {
                EmptyPokeBallEntity.CaptureState.HIT -> {
                    doLater {
                        val model = this.currentModel!!
                        after(seconds = 0.2F) {
                            if (model is PokeBallModel && stateEmitter.get() == EmptyPokeBallEntity.CaptureState.HIT) {
                                doLater latest@{
                                    val entity = model.context.request(RenderContext.ENTITY) as EmptyPokeBallEntity? ?: return@latest
                                    model.moveToPose(entity, this, model.open)
                                    after(seconds = 1.75F) {
                                        model.moveToPose(entity, this, model.shut)
                                    }
                                }
                            }
                        }
                    }
                }

                EmptyPokeBallEntity.CaptureState.FALL -> {}
                EmptyPokeBallEntity.CaptureState.SHAKE -> {
                    doLater {
                        setStatefulAnimations(currentModel!!.bedrockStateful(group, "bounce").setPreventsIdle(false))
                    }
                    shakeEmitter
                        .pipe(Observable.emitWhile { stateEmitter.get() == EmptyPokeBallEntity.CaptureState.SHAKE })
                        .subscribe {
                            val bob = "bob${Random.Default.nextInt(6) + 1}"
                            doLater { setStatefulAnimations(currentModel!!.bedrockStateful(group, bob).setPreventsIdle(false)) }
                        }
                }
                EmptyPokeBallEntity.CaptureState.CAPTURED -> {
                    doLater {
                        setStatefulAnimations(currentModel!!.bedrockStateful(group, "capture").setPreventsIdle(false))
                    }
                }
                EmptyPokeBallEntity.CaptureState.CAPTURED_CRITICAL -> {
                    doLater {
                        setStatefulAnimations(currentModel!!.bedrockStateful(group, "critical").setPreventsIdle(false))
                    }
                }
                else -> {}
            }
        }
    }
}