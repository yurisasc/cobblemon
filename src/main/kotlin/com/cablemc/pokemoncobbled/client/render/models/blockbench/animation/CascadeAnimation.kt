package com.cablemc.pokemoncobbled.client.render.models.blockbench.animation

import com.cablemc.pokemoncobbled.client.render.models.blockbench.PoseableEntityModel
import com.cablemc.pokemoncobbled.client.render.models.blockbench.frame.ModelFrame
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.world.entity.Entity
import kotlin.math.cos

/**
 * A cascading animation that will increase movement over chained parts
 *
 * @author Deltric
 * @since December 21st, 2021
 */
class CascadeAnimation<T : Entity>(
    frame: ModelFrame,
    val cosineFunction: CosineFunction,
    val amplitudeFunction: AmplitudeFunction,
    val segments: Array<ModelPart>
): StatelessAnimation<T, ModelFrame>(frame) {

    override val targetFrame = ModelFrame::class.java

    override fun setAngles(entity: T?, model: PoseableEntityModel<T>, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float, headYaw: Float, headPitch: Float) {
        segments.forEachIndexed { index, modelPart ->
            modelPart.yRot = cosineFunction(ageInTicks) * amplitudeFunction(index+1)
        }
    }

}

typealias AmplitudeFunction = (Int) -> Float
typealias CosineFunction = (Float) -> Float

fun gradualFunction(base: Float = 1F, step: Float = 1F): AmplitudeFunction = { index ->
    base + step * index
}

fun cosineFunction(phaseShift: Float = 1F): CosineFunction = { ageInTicks ->
    cos(ageInTicks * phaseShift)
}