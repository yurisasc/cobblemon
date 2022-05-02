package com.cablemc.pokemoncobbled.common.client.gui

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.PoseableEntityState
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.repository.PokemonModelRepository
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.DiffuseLighting
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Quaternion
import net.minecraft.util.math.Vec3f

fun drawProfilePokemon(
    pokemon: Pokemon,
    matrixStack: MatrixStack,
    rotation: Quaternion,
    state: PoseableEntityState<PokemonEntity>?,
    scale: Float = 20F
) {
    val model = PokemonModelRepository.getModel(pokemon).entityModel
    val texture = PokemonModelRepository.getModelTexture(pokemon)

    val renderType = model.getLayer(texture)

    RenderSystem.applyModelViewMatrix()
    matrixStack.scale(scale, scale, -scale)

    if (model is PokemonPoseableModel) {
        if (state != null) {
            model.getPose(PoseType.PROFILE)?.let { state.setPose(it.poseName) }
            model.setupAnimStateful(null, state, 0F, 0F, 0F, 0F, 0F)
        } else {
            model.setupAnimStateless(PoseType.PROFILE)
        }
//        model.setupAnimStateless(PoseType.PROFILE)
        matrixStack.translate(model.profileTranslation.x, model.profileTranslation.y, -10.0)
        matrixStack.scale(model.profileScale, model.profileScale, 0.01F)
    }

    matrixStack.multiply(rotation)
    DiffuseLighting.method_34742()
    val entityRenderDispatcher = MinecraftClient.getInstance().entityRenderDispatcher
    rotation.conjugate()
    entityRenderDispatcher.rotation = rotation
    entityRenderDispatcher.setRenderShadows(true)

    val bufferSource = MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers
    val buffer = bufferSource.getBuffer(renderType)
    val light1 = Vec3f(-1F, 1F, 1.0F)
    val light2 = Vec3f(1.3F, -1F, 1.0F)
    RenderSystem.setShaderLights(light1, light2)
    val packedLight = LightmapTextureManager.pack(8, 6)
    model.render(matrixStack, buffer, packedLight, OverlayTexture.DEFAULT_UV, 1F, 1F, 1F, 1F)
    bufferSource.draw()
    entityRenderDispatcher.setRenderShadows(true)
    DiffuseLighting.enableGuiDepthLighting()
}

