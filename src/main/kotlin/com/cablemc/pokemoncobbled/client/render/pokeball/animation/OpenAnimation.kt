package com.cablemc.pokemoncobbled.client.render.pokeball.animation

import com.cablemc.pokemoncobbled.client.render.models.blockbench.PoseableEntityModel
import com.cablemc.pokemoncobbled.client.render.models.blockbench.animation.StatefulAnimation
import com.cablemc.pokemoncobbled.client.render.models.blockbench.animation.StatelessAnimation
import com.cablemc.pokemoncobbled.client.render.models.blockbench.frame.PokeBallFrame
import com.cablemc.pokemoncobbled.common.entity.pokeball.PokeBallEntity
import com.cablemc.pokemoncobbled.common.util.math.geometry.toRadians
import net.minecraft.util.Mth.PI

class OpenAnimation(frame: PokeBallFrame) : StatefulAnimation<PokeBallEntity, PokeBallFrame>(frame) {
    companion object {
        const val DURATION_SECONDS = 3F
    }

    var initialized = false
    override fun preventsIdle(entity: PokeBallEntity, idleAnimation: StatelessAnimation<PokeBallEntity, *>) = true
    override fun run(entity: PokeBallEntity, model: PoseableEntityModel<PokeBallEntity>): Boolean {
        if (!initialized) {
            model.getState(entity).animationSeconds = 0F
            initialized = true
        }

        val animationSeconds = model.getState(entity).animationSeconds

        when {
            animationSeconds <= 1 -> {
                frame.lid.xRot = -animationSeconds * PI / 3
            }
            animationSeconds > 2 -> {
                frame.lid.xRot = -30f.toRadians() + ((animationSeconds - 2) * (1.5f).toRadians()) / 20F
            }
            else -> {
                frame.lid.xRot = 30f.toRadians()
                return false
            }
        }

        return true
    }
}