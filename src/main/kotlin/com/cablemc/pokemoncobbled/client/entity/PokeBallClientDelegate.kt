package com.cablemc.pokemoncobbled.client.entity

import com.cablemc.pokemoncobbled.client.render.models.blockbench.PoseableEntityState
import com.cablemc.pokemoncobbled.client.render.models.blockbench.frame.PokeBallFrame
import com.cablemc.pokemoncobbled.client.render.pokeball.animation.OpenAnimation
import com.cablemc.pokemoncobbled.common.api.entity.EntitySideDelegate
import com.cablemc.pokemoncobbled.common.entity.pokeball.EmptyPokeBallEntity
import com.cablemc.pokemoncobbled.common.entity.pokeball.EmptyPokeBallEntity.CaptureState
import com.cablemc.pokemoncobbled.common.entity.pokeball.EmptyPokeBallEntity.CaptureState.HIT
import com.cablemc.pokemoncobbled.common.entity.pokeball.EmptyPokeBallEntity.CaptureState.SHAKE
import com.cablemc.pokemoncobbled.common.entity.pokeball.PokeBallEntity

class PokeBallClientDelegate : PoseableEntityState<PokeBallEntity>(), EntitySideDelegate<PokeBallEntity> {
    override fun initialize(entity: PokeBallEntity) {
        if (entity is EmptyPokeBallEntity) {
            entity.captureState.subscribe {
                when (CaptureState.values()[it.toInt()]) {
                    HIT -> {
                        statefulAnimations.add(OpenAnimation(currentModel as PokeBallFrame))
                    }
                    CaptureState.FALL -> {

                    }
                    SHAKE -> {

                    }
                    else -> {}
                }
            }
        }
    }
}