package com.cablemc.pokemoncobbled.client.entity

import com.cablemc.pokemoncobbled.client.render.models.blockbench.PoseableEntityState
import com.cablemc.pokemoncobbled.client.render.pokeball.animation.OpenAnimation
import com.cablemc.pokemoncobbled.client.render.pokeball.animation.ShakeAnimation
import com.cablemc.pokemoncobbled.common.api.entity.EntitySideDelegate
import com.cablemc.pokemoncobbled.common.api.reactive.Observable.Companion.emitWhile
import com.cablemc.pokemoncobbled.common.entity.pokeball.EmptyPokeBallEntity
import com.cablemc.pokemoncobbled.common.entity.pokeball.EmptyPokeBallEntity.CaptureState
import com.cablemc.pokemoncobbled.common.entity.pokeball.EmptyPokeBallEntity.CaptureState.HIT
import com.cablemc.pokemoncobbled.common.entity.pokeball.EmptyPokeBallEntity.CaptureState.SHAKE

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