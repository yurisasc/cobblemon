package com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.animation

import com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.PoseableEntityModel
import com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.frame.PokeBallFrame
import com.cablemc.pokemoncobbled.common.entity.pokeball.EmptyPokeBallEntity
import net.minecraft.util.Mth
import net.minecraft.util.Mth.PI

/**
 * A simple idle animation that will point the PokéBall in the direction of the target it hit. Does nothing
 * for occupied PokéBalls.
 *
 * @author Hiroku
 * @since December 25th, 2021
 */
class RootPokeBallLookAnimation(frame: PokeBallFrame) : StatelessAnimation<EmptyPokeBallEntity, PokeBallFrame>(frame) {
    override val targetFrame = PokeBallFrame::class.java
    override fun setAngles(entity: EmptyPokeBallEntity?, model: PoseableEntityModel<EmptyPokeBallEntity>, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float, headYaw: Float, headPitch: Float) {
        val yRot = entity?.let {
            val dispX = it.hitTargetPosition.get().x - it.x
            val dispZ = it.hitTargetPosition.get().z - it.z
            Mth.atan2(-dispZ, dispX) + PI / 2
        }?.toFloat() ?: 0F

        model.rootPart.yRot = yRot
    }
}