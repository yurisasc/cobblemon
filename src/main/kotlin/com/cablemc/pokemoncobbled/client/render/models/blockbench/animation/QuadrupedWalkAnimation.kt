package com.cablemc.pokemoncobbled.client.render.models.blockbench.animation

import com.cablemc.pokemoncobbled.client.render.models.blockbench.frame.QuadrupedFrame
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pose.Pose
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import net.minecraft.util.Mth

class QuadrupedWalkAnimation(
    val periodMultiplier: Float = 0.6662F,
    val amplitudeMultiplier: Float = 1.4F
) : StatelessAnimation<PokemonEntity, QuadrupedFrame> {
    override fun setAngles(entity: PokemonEntity, frame: QuadrupedFrame, pose: Pose<PokemonEntity, QuadrupedFrame>, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float) {
        frame.hindRightLeg.xRot = Mth.cos(limbSwing * periodMultiplier) * limbSwingAmount * amplitudeMultiplier
        frame.hindLeftLeg.xRot = Mth.cos(limbSwing * periodMultiplier + Math.PI.toFloat()) * limbSwingAmount * amplitudeMultiplier
        frame.foreRightLeg.xRot = Mth.cos(limbSwing * periodMultiplier + Math.PI.toFloat()) * limbSwingAmount * amplitudeMultiplier
        frame.foreLeftLeg.xRot = Mth.cos(limbSwing * periodMultiplier) * limbSwingAmount * amplitudeMultiplier
    }
}