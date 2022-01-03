package com.cablemc.pokemoncobbled.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.client.entity.PokemonClientDelegate
import com.cablemc.pokemoncobbled.client.render.models.blockbench.PoseableEntityModel
import com.cablemc.pokemoncobbled.client.render.models.blockbench.animation.StatelessAnimation
import com.cablemc.pokemoncobbled.client.render.models.blockbench.frame.ModelFrame
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pose.TransformedModelPart
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer

/**
 * A poseable model for a Pokémon. Just handles the state accessor to the [PokemonClientDelegate].
 *
 * @author Hiroku
 * @since December 4th, 2021
 */
abstract class PokemonPoseableModel : PoseableEntityModel<PokemonEntity>() {
    override fun getState(entity: PokemonEntity) = entity.delegate as PokemonClientDelegate

    var red = 1F
    var green = 1F
    var blue = 1F
    var alpha = 1F

    /** Registers the same configuration for both left and right shoulder poses. */
    fun <F : ModelFrame> registerShoulderPoses(
        condition: (PokemonEntity) -> Boolean = { true },
        transformTicks: Int = 30,
        idleAnimations: Array<StatelessAnimation<PokemonEntity, out F>>,
        transformedParts: Array<TransformedModelPart>
    ) {
        registerPose(
            poseType = PoseType.SHOULDER_LEFT,
            condition = condition,
            transformTicks = transformTicks,
            idleAnimations = idleAnimations,
            transformedParts = transformedParts
        )

        registerPose(
            poseType = PoseType.SHOULDER_RIGHT,
            condition = condition,
            transformTicks = transformTicks,
            idleAnimations = idleAnimations,
            transformedParts = transformedParts
        )
    }

    override fun renderToBuffer(stack: PoseStack, buffer: VertexConsumer, packedLight: Int, packedOverlay: Int, r: Float, g: Float, b: Float, a: Float) {
        super.renderToBuffer(stack, buffer, packedLight, packedOverlay, red * r, green * g, blue * b, alpha * a)
    }
}