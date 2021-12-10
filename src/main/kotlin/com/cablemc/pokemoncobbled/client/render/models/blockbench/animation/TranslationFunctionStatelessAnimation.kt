package com.cablemc.pokemoncobbled.client.render.models.blockbench.animation

import com.cablemc.pokemoncobbled.client.render.models.blockbench.PoseableEntityModel
import com.cablemc.pokemoncobbled.client.render.models.blockbench.frame.ModelFrame
import com.cablemc.pokemoncobbled.client.render.models.blockbench.setPosition
import com.cablemc.pokemoncobbled.client.render.models.blockbench.wavefunction.LineFunction
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.world.entity.Entity

/**
 * Animation simply works by moving a part along a particular function
 */
class TranslationFunctionStatelessAnimation<T : Entity>(
    val part: ModelPart,
    val function: LineFunction,
    val axis: Int,
    frame: ModelFrame
) : StatelessAnimation<T, ModelFrame>(frame) {
    override val targetFrame = ModelFrame::class.java
    override fun setAngles(entity: T?, model: PoseableEntityModel<T>, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float, headYaw: Float, headPitch: Float) {
        val animationTick = entity?.let { model.getState(it).animationTick } ?: 0F
        part.setPosition(axis, function(animationTick))
    }
}