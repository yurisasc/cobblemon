package com.cablemc.pokemoncobbled.client.render.pokeball.animation

import com.cablemc.pokemoncobbled.client.render.models.blockbench.pokeball.PokeBallModel
import com.cablemc.pokemoncobbled.common.util.math.geometry.toRadians
import net.minecraft.util.Mth

class ShakeAnimation(val numShakes: Int) : ModelAnimation<PokeBallModel> {

    override var currentFrame: Int? = null
        private set
    private var shakesComplete = 0

    override fun animate(
        model: PokeBallModel,
        limbSwing: Float,
        limbSwingAmout: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float,
        frame: Int
    ) {
        currentFrame = frame % 63
        model.rootPart.y = 20f
        model.rootPart.xRot = 180f.toRadians()
        model.rootPart.yRot = 0f
        model.lid.xRot = 0f
        if (shakesComplete < numShakes) {
            if (currentFrame == 62) {
                currentFrame = 0
                shakesComplete++
            }
            if (currentFrame!! <= 42) {
                model.rootPart.zRot = Mth.sin(currentFrame!!.toFloat() * 0.15f) * 30f.toRadians()
            }
            else {
                model.rootPart.zRot = 0f
            }
        }
        else {
            model.rootPart.zRot = 0f
        }
    }

    override fun resetAnimation() {
        currentFrame = 0
    }
}