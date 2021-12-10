package com.cablemc.pokemoncobbled.client.render.pokeball.animation

import com.cablemc.pokemoncobbled.client.render.models.blockbench.pokeball.PokeBallModel
import com.cablemc.pokemoncobbled.common.util.math.geometry.toRadians

class SpinAnimation : ModelAnimation<PokeBallModel> {

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
        currentFrame = frame % 46
        model.pokeball.y = 20f
        model.pokeball.yRot = frame * 8f.toRadians()
        model.pokeball.xRot = 180f.toRadians()
        model.pokeball.zRot = 0f
        model.pokeballLid.xRot = 0f
    }

    override fun resetAnimation() {
        currentFrame = 0
    }

}