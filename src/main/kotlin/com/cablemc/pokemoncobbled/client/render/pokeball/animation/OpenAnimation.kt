package com.cablemc.pokemoncobbled.client.render.pokeball.animation

import com.cablemc.pokemoncobbled.client.render.models.blockbench.pokeball.PokeBallModel
import com.cablemc.pokemoncobbled.common.util.math.geometry.toRadians

class OpenAnimation : ModelAnimation<PokeBallModel> {

    override var currentFrame: Int = 0
        private set

    override fun animate(
        model: PokeBallModel,
        limbSwing: Float,
        limbSwingAmout: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float,
        frame: Int
    ) {
        currentFrame = frame
        model.pokeballLid.zRot = 0f
        model.pokeballLid.yRot = 0f
        when {
            currentFrame <= 20 -> {
                model.pokeballLid.xRot = 0 + (currentFrame * (-1.5f).toRadians())
            }
            currentFrame >= 41 -> {
                model.pokeballLid.xRot = -30f.toRadians() + ((currentFrame - 41) * (1.5f).toRadians())
            }
            else -> {
                model.pokeballLid.xRot = -30f.toRadians()
            }
        }
    }

    override fun resetAnimation() {
        currentFrame = 0
    }

    fun isComplete() : Boolean = currentFrame >= 60

}