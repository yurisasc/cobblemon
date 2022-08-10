package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.entity.PokemonClientDelegate
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.PoseableEntityModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.PoseableEntityState
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.StatefulAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.StatelessAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.ModelFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.Pose
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart
import com.cablemc.pokemoncobbled.common.client.render.pokemon.ModelLayer
import com.cablemc.pokemoncobbled.common.entity.PoseType
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Vec3d

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

    @Transient
    var currentLayers = listOf<ModelLayer>()
    @Transient
    var bufferProvider: VertexConsumerProvider? = null

    fun withLayerContext(buffer: VertexConsumerProvider, layers: List<ModelLayer>, action: () -> Unit) {
        setLayerContext(buffer, layers)
        action()
        resetLayerContext()
    }

    fun setLayerContext(buffer: VertexConsumerProvider, layers: List<ModelLayer>) {
        currentLayers = layers
        bufferProvider = buffer
    }
    fun resetLayerContext() {
        currentLayers = emptyList()
        bufferProvider = null
    }

    /** Registers the same configuration for both left and right shoulder poses. */
    fun <F : ModelFrame> registerShoulderPoses(
        transformTicks: Int = 30,
        idleAnimations: Array<StatelessAnimation<PokemonEntity, out F>>,
        transformedParts: Array<TransformedModelPart> = emptyArray()
    ) {
        registerPose(
            poseType = PoseType.SHOULDER_LEFT,
            transformTicks = transformTicks,
            idleAnimations = idleAnimations,
            transformedParts = transformedParts
        )

        registerPose(
            poseType = PoseType.SHOULDER_RIGHT,
            transformTicks = transformTicks,
            idleAnimations = idleAnimations,
            transformedParts = transformedParts
        )
    }

    override fun render(stack: MatrixStack, buffer: VertexConsumer, packedLight: Int, packedOverlay: Int, r: Float, g: Float, b: Float, a: Float) {
        super.render(stack, buffer, packedLight, OverlayTexture.DEFAULT_UV, red * r, green * g, blue * b, alpha * a)

        val provider = bufferProvider
        if (provider != null) {
            for (layer in currentLayers) {
                val texture = layer.texture ?: continue
                val consumer = provider.getBuffer(RenderLayer.getEntityTranslucent(texture))
                stack.push()
                stack.scale(layer.scale.x, layer.scale.y, layer.scale.z)
                super.render(stack, consumer, packedLight, OverlayTexture.DEFAULT_UV, layer.tint.x, layer.tint.y, layer.tint.z, layer.tint.w)
                stack.pop()
            }
        }
    }

    @Transient
    open val portraitScale: Float = 1F
    @Transient
    open val portraitTranslation: Vec3d = Vec3d.ZERO

    @Transient
    open val profileScale: Float = 1F
    @Transient
    open val profileTranslation: Vec3d = Vec3d.ZERO

    open fun getFaintAnimation(
        pokemonEntity: PokemonEntity,
        state: PoseableEntityState<PokemonEntity>
    ): StatefulAnimation<PokemonEntity, ModelFrame>? = null
}

typealias PokemonPose = Pose<PokemonEntity, ModelFrame>