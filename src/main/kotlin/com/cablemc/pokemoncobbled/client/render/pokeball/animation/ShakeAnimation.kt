package com.cablemc.pokemoncobbled.client.render.pokeball.animation

import com.cablemc.pokemoncobbled.client.render.models.blockbench.pokeball.PokeBallModel
import com.cablemc.pokemoncobbled.common.util.math.geometry.toRadians
import net.minecraft.util.Mth

class ShakeAnimation : ModelAnimation<PokeBallModel> {

    override var currentFrame: Int? = null
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
        if (currentFrame!! >= 60) {
            currentFrame = 0
        }

        model.pokeball.xRot = 180f.toRadians()
        if (currentFrame!! <= 42) {
            model.pokeball.zRot = Mth.sin(currentFrame!!.toFloat() * 0.15f) * 30f.toRadians()
        }
        else {
            model.pokeball.zRot = 0f
        }
    }

    override fun resetAnimation() {
        currentFrame = 0
    }

}